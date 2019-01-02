package com.github.takezoe.rison

sealed trait AST
sealed trait ValueNode extends AST

case class StringNode(value: String) extends ValueNode
case class IntNode(value: Int) extends ValueNode
case class BooleanNode(value: Boolean) extends ValueNode
case class NullNode() extends ValueNode
case class PropertyNode(key: StringNode, value: ValueNode)
case class ObjectNode(values: Seq[PropertyNode]) extends ValueNode
case class ArrayNode(values: Seq[ValueNode]) extends ValueNode
