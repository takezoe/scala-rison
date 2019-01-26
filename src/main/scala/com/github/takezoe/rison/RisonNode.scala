package com.github.takezoe.rison

import java.net.URLEncoder

import wvlet.airframe.surface.Surface
import wvlet.airframe.surface.reflect.ReflectTypeUtil

import scala.reflect.runtime.universe._

object RisonNode {

  def fromScala[T: TypeTag](value: T): RisonNode = {
    val surface = Surface.of[T]
    encodeValue(value, surface)
  }

  private def encodeValue(value: Any, surface: Surface): RisonNode = {
    if(surface.name == "Any"){ // fallback reflection mode
      encodePrimitive(value)

    } else if(surface.isPrimitive){
      encodePrimitive(value)

    } else if(surface.isOption){
      value match {
        case None    => NullNode()
        case Some(x) => encodeValue(x, surface.typeArgs(0))
      }
    } else if(ReflectTypeUtil.isSeq(surface.rawType)){
      ArrayNode(value.asInstanceOf[Seq[_]].map { x =>
        encodeValue(x, surface.typeArgs(0)).asInstanceOf[ValueNode]
      })
    } else if(ReflectTypeUtil.isMap(surface.rawType)){
      ObjectNode(value.asInstanceOf[Map[String, _]].map { case (name, x) =>
          PropertyNode(StringNode(name), encodeValue(x, surface.typeArgs(1)).asInstanceOf[ValueNode])
      }.toSeq)
    } else {
      ObjectNode(surface.params.map { p =>
        PropertyNode(StringNode(p.name), encodeValue(p.get(value), p.surface).asInstanceOf[ValueNode])
      })
    }
  }

  private def encodePrimitive(value: Any): RisonNode = {
    value match {
      case null                       => NullNode()
      case x: String                  => StringNode(x)
      case x: Int                     => LongNode(x)
      case x: Long                    => LongNode(x)
      case x: Double                  => DoubleNode(x)
      case x: Float                   => DoubleNode(x)
      case x: Boolean                 => BooleanNode(x)
      case (name: String, value: Any) => PropertyNode(StringNode(name), encodePrimitive(value).asInstanceOf[ValueNode])
      case x: Map[String, _]          => ObjectNode (x.map { e => encodePrimitive(e).asInstanceOf[PropertyNode] }.toSeq)
      case x: Seq[_]                  => ArrayNode  (x.map { e => encodePrimitive(e).asInstanceOf[ValueNode] })
    }
  }

  private def urlEncode(str: String): String = {
    URLEncoder.encode(str, "UTF-8")
      .replace("%7E", "~")
      .replace("%21", "!")
      .replace("%2A", "*")
      .replace("%28", "(")
      .replace("%29", ")")
      .replace("%2D", "-")
      .replace("%5F", "_")
      .replace("%2E", ".")
      .replace("%2C", ",")
      .replace("%3A", ":")
      .replace("%40", "@")
      .replace("%24", "$")
      .replace("%27", "'")
      .replace("%2F", "/")
      .replace("%20", "+")
  }
}

sealed trait RisonNode {
  def toScala: Any
  def toRisonString: String
  def toUrlEncodedString: String = RisonNode.urlEncode(toRisonString)
}

sealed trait ValueNode extends RisonNode {
  def ofSurface(surface: Surface): Any
}

case class StringNode(value: String) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = quote(value)

  private val RequiredQuote = Seq(" ", "\t", "\r", "\n", "'", "!", ":", "(", ")", ",", "*", "@", "$")

  private def quote(str: String): String = {
    if(RequiredQuote.exists(c => str.indexOf(c) != -1)){
      "'" + str.replace("!", "!!").replace("'", "!'") + "'"
    } else str
  }

  def ofSurface(surface: Surface): Any = value
}

case class LongNode(value: Long) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = value.toString
  def ofSurface(surface: Surface): Any = {
    if(surface.name == "Int") value.toInt
    else                      value
  }
}

case class DoubleNode(value: Double) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = value.toString
  def ofSurface(surface: Surface): Any = {
    if      (surface.name == "Float") value.toFloat
    else if (surface.name == "Short") value.toShort
    else                              value
  }}

case class BooleanNode(value: Boolean) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = if(value) "!t" else "!f"
  def ofSurface(surface: Surface): Any = value
}

case class NullNode() extends ValueNode {
  override def toScala: Any = null
  override def toRisonString: String = "!n"
  def ofSurface(surface: Surface): Any = null
}

case class PropertyNode(key: StringNode, value: ValueNode) extends RisonNode {
  override def toScala: Any = (key.value, value.toScala)
  override def toRisonString: String = key.toRisonString + ":" + value.toRisonString
}

case class ObjectNode(values: Seq[PropertyNode]) extends ValueNode {
  override def toScala: Any = Map[String, Any](values.map { x => x.key.value -> x.value.toScala }:_*)
  override def toRisonString: String = "(" + toObjectString + ")"
  def toObjectString: String = values.map(_.toRisonString).mkString(",")

  def ofSurface(surface: Surface): Any = {
    val params = values.map { case PropertyNode(StringNode(name), value) =>
      val param = surface.params.find(_.name == name).get
      value match {
        case x: ArrayNode  => x.ofSurface(param.surface.typeArgs(0))
        case x             => x.ofSurface(param.surface)
      }
    }
    surface.objectFactory.get.newInstance(params)
  }

  def to[T: TypeTag]: T = {
    val surface = Surface.of[T]
    ofSurface(surface).asInstanceOf[T]
  }
}

case class ArrayNode(values: Seq[ValueNode]) extends ValueNode {
  override def toScala: Any = Seq[Any](values.map(_.toScala):_*)
  override def toRisonString: String = "!(" + toArrayString + ")"
  def toArrayString: String = values.map(_.toRisonString).mkString(",")

  def ofSurface(surface: Surface): Any = {
    Seq(values.map { case value: ValueNode =>
      value match {
        case x: ArrayNode  => x.ofSurface(surface.typeArgs(0))
        case x             => x.ofSurface(surface)
      }
    })
  }
}
