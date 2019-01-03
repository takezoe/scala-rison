package com.github.takezoe.rison

import scala.util.parsing.combinator._

sealed trait AST {
  def toScala: Any
}
sealed trait ValueNode extends AST
case class StringNode(value: String) extends ValueNode {
  override def toScala: Any = value
}
case class IntNode(value: Int) extends ValueNode {
  override def toScala: Any = value
}
case class BooleanNode(value: Boolean) extends ValueNode {
  override def toScala: Any = value
}
case class NullNode() extends ValueNode {
  override def toScala: Any = null // or None?
}
case class PropertyNode(key: StringNode, value: ValueNode) extends AST {
  override def toScala: Any = key.value -> value.toScala
}
case class ObjectNode(values: Seq[PropertyNode]) extends ValueNode {
  override def toScala: Any = Map[String, Any](values.map { property =>
    property.key.value -> property.value.toScala
  }:_*)
}
case class ArrayNode(values: Seq[ValueNode]) extends ValueNode {
  override def toScala: Any = Seq[Any](values.map(_.toScala):_*)
}

class RisonParser extends RegexParsers {

  override def skipWhitespace: Boolean = false

  def int: Parser[IntNode]       = "[0-9]+".r           ^^ { x => IntNode(x.toInt) }
  def string: Parser[StringNode] = "[^!:(),*@$\\s']+".r ^^ { x => StringNode(x) }
  def t: Parser[BooleanNode]     = "!t"                 ^^^ BooleanNode(true)
  def f: Parser[BooleanNode]     = "!f"                 ^^^ BooleanNode(false)
  def n: Parser[NullNode]        = "!n"                 ^^^ NullNode()
  def quoted: Parser[StringNode] = "'" ~> "((!')|(!!)|[^'])*".r <~ "'"  ^^ { x => StringNode(unescape(x)) }
  def obj: Parser[ObjectNode]    = "("  ~> repsep(property, ",") <~ ")" ^^ { x => ObjectNode(x) }
  def array: Parser[ArrayNode]   = "!(" ~> repsep(value,    ",") <~ ")" ^^ { x => ArrayNode(x) }
  def value: Parser[ValueNode]   = int | string | quoted | t | f | n | obj | array
  def property: Parser[PropertyNode] = string ~ ":" ~ value ^^ { case name ~ _ ~ value => PropertyNode(name, value) }

  def rison: Parser[ValueNode] = value

  private def unescape(str: String): String = {
    val sb = new StringBuilder()
    var escaping = false
    str.foreach {
      case '!'  if escaping == false => escaping = true
      case '!'  if escaping == true  => sb.append('!'); escaping = false
      case '\'' if escaping == true  => sb.append('\''); escaping = false
      case c    if escaping == true  => throw new RisonParseException(s"Invalid Escape: !${c}")
      case c                         => sb.append(c)
    }
    if(escaping){
      throw new RisonParseException(s"Invalid Escape: !")
    }
    sb.toString
  }

  def parse(str: String): Either[String, ValueNode] = {
    try {
      parse(rison, str) match {
        case Success(matched, _) => Right(matched)
        case Failure(msg    , _) => Left(msg)
        case Error  (msg    , _) => Left(msg)
      }
    } catch {
      case e: RisonParseException => Left(e.getMessage)
    }
  }

  private class RisonParseException(message: String) extends RuntimeException(message)

}