package com.github.takezoe.rison

import java.net.URLEncoder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.reflect.ClassTag


object RisonNode {

  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def fromScala(value: Any): RisonNode = {
    value match {
      case null                       => NullNode()
      case x: String                  => StringNode(x)
      case x: Int                     => LongNode(x)
      case x: Long                    => LongNode(x)
      case x: Double                  => DoubleNode(x)
      case x: Float                   => DoubleNode(x)
      case x: Boolean                 => BooleanNode(x)
      case (name: String, value: Any) => PropertyNode(StringNode(name), fromScala(value).asInstanceOf[ValueNode])
      case x: Map[String, _]          => ObjectNode (x.map { e => fromScala(e).asInstanceOf[PropertyNode] }.toSeq)
      case x: Seq[_]                  => ArrayNode  (x.map { e => fromScala(e).asInstanceOf[ValueNode] })
      case x => {
        val json = mapper.writeValueAsString(x)
        fromScala(mapper.readValue(json, classOf[Map[String, Any]]))
      }
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

sealed trait ValueNode extends RisonNode

case class StringNode(value: String) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = quote(value)

  private val RequiredQuote = Seq(" ", "\t", "\r", "\n", "'", "!", ":", "(", ")", ",", "*", "@", "$")

  private def quote(str: String): String = {
    if(RequiredQuote.exists(c => str.indexOf(c) != -1)){
      "'" + str.replace("!", "!!").replace("'", "!'") + "'"
    } else str
  }
}

case class LongNode(value: Long) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = value.toString
}

case class DoubleNode(value: Double) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = value.toString
}

case class BooleanNode(value: Boolean) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = if(value) "!t" else "!f"
}

case class NullNode() extends ValueNode {
  override def toScala: Any = null
  override def toRisonString: String = "!n"
}

case class PropertyNode(key: StringNode, value: ValueNode) extends RisonNode {
  override def toScala: Any = (key.value, value.toScala)
  override def toRisonString: String = key.toRisonString + ":" + value.toRisonString
}

case class ObjectNode(values: Seq[PropertyNode]) extends ValueNode {
  def to[T](implicit c: ClassTag[T]): T = {
    val json = RisonNode.mapper.writeValueAsString(toScala)
    RisonNode.mapper.readValue(json, c.runtimeClass).asInstanceOf[T]
  }
  override def toScala: Any = Map[String, Any](values.map { x => x.key.value -> x.value.toScala }:_*)
  override def toRisonString: String = "(" + toObjectString + ")"
  def toObjectString: String = values.map(_.toRisonString).mkString(",")
}

case class ArrayNode(values: Seq[ValueNode]) extends ValueNode {
  override def toScala: Any = Seq[Any](values.map(_.toScala):_*)
  override def toRisonString: String = "!(" + toArrayString + ")"
  def toArrayString: String = values.map(_.toRisonString).mkString(",")
}
