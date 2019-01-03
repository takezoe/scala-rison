package com.github.takezoe.rison

object RisonNode {

  def fromScala(value: Any): RisonNode = {
    value match {
      case null                       => NullNode()
      case x: String                  => StringNode(x)
      case x: Int                     => IntNode(x)
      case x: Boolean                 => BooleanNode(x)
      case (name: String, value: Any) => PropertyNode(StringNode(name), fromScala(value).asInstanceOf[ValueNode])
      case x: Map[String, _]          => ObjectNode (x.map { e => fromScala(e).asInstanceOf[PropertyNode] }.toSeq)
      case x: Seq[_]                  => ArrayNode  (x.map { e => fromScala(e).asInstanceOf[ValueNode]    })
    }
  }

}

sealed trait RisonNode {
  def toScala: Any
  def toRisonString: String
}

sealed trait ValueNode extends RisonNode

case class StringNode(value: String) extends ValueNode {
  override def toScala: Any = value
  override def toRisonString: String = quote(value)

  private val RequiredQuote = Seq("'", " ", "\t", "\r", "\n")

  private def quote(str: String): String = {
    if(RequiredQuote.exists(c => str.indexOf(c) != -1)){
      "'" + str.replace("!", "!!").replace("'", "!'") + "'"
    } else str
  }
}

case class IntNode(value: Int) extends ValueNode {
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
  override def toScala: Any = Map[String, Any](values.map { x => x.key.value -> x.value.toScala }:_*)
  override def toRisonString: String = "(" + values.map(_.toRisonString).mkString(",") + ")"
}

case class ArrayNode(values: Seq[ValueNode]) extends ValueNode {
  override def toScala: Any = Seq[Any](values.map(_.toScala):_*)
  override def toRisonString: String = "!(" + values.map(_.toRisonString).mkString(",") + ")"
}
