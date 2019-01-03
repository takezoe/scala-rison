package com.github.takezoe.rison

import scala.util.parsing.combinator._

class Parser extends RegexParsers {

  override def skipWhitespace: Boolean = false

  def integer: Parser[IntNode]   = "[0-9]+".r           ^^ { x => IntNode(x.toInt) }
  def string: Parser[StringNode] = "[^!:(),*@$\\s']+".r ^^ { x => StringNode(x) }
  def t: Parser[BooleanNode]     = "!t"                 ^^^ BooleanNode(true)
  def f: Parser[BooleanNode]     = "!f"                 ^^^ BooleanNode(false)
  def n: Parser[NullNode]        = "!n"                 ^^^ NullNode()
  def quoted: Parser[StringNode] = "'" ~> "((!')|(!!)|[^'])*".r <~ "'"  ^^ { x => StringNode(unescape(x)) }
  def obj: Parser[ObjectNode]    = "("  ~> repsep(property, ",") <~ ")" ^^ { x => ObjectNode(x) }
  def array: Parser[ArrayNode]   = "!(" ~> repsep(value,    ",") <~ ")" ^^ { x => ArrayNode(x) }
  def value: Parser[ValueNode]   = integer | string | quoted | t | f | n | obj | array
  def property: Parser[PropertyNode] = string ~ ":" ~ value ^^ { case name ~ _ ~ value => PropertyNode(name, value) }

  def rison: Parser[ValueNode] = value

  private def unescape(str: String): String = {
    val sb = new StringBuilder()
    var escaping = false
    str.foreach {
      case '!'  if escaping == false => escaping = true
      case '!'  if escaping == true  => sb.append('!'); escaping = false
      case '\'' if escaping == true  => sb.append('\''); escaping = false
      case c    if escaping == true  => throw new RuntimeException(s"Invalid Escape: !${c}")
      case c                         => sb.append(c)
    }
    sb.toString
  }
}
