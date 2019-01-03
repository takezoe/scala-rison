package com.github.takezoe.rison

object RisonConverter {

  def toScala(node: AST): Any = {
    node match {
      case StringNode(value)         => value
      case IntNode(value)            => value
      case BooleanNode(value)        => value
      case NullNode()                => null
      case PropertyNode(name, value) => (name.value, toScala(value))
      case ObjectNode(values)        => Map[String, Any](values.map { x => x.key.value -> toScala(x.value) }:_*)
      case ArrayNode(values)         => Seq[Any](values.map(toScala(_)):_*)
    }
  }

  def toRison(value: Any): AST = {
    value match {
      case null                       => NullNode()
      case x: String                  => StringNode(x)
      case x: Int                     => IntNode(x)
      case x: Boolean                 => BooleanNode(x)
      case (name: String, value: Any) => PropertyNode(StringNode(name), toRison(value).asInstanceOf[ValueNode])
      case x: Map[String, _]          => ObjectNode (x.map { e => toRison(e).asInstanceOf[PropertyNode] }.toSeq)
      case x: Seq[_]                  => ArrayNode  (x.map { e => toRison(e).asInstanceOf[ValueNode]    })

    }
  }

}