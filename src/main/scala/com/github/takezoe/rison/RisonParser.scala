package com.github.takezoe.rison

import scala.util.parsing.combinator._

object RisonParser extends RegexParsers {

  def long: Parser[LongNode]     = "[1-9][0-9]*".r      ^^ { x => LongNode(x.toLong) }
  def double: Parser[DoubleNode] = "[0-9]+\\.[0-9]+".r  ^^ { x => DoubleNode(x.toDouble) }
  def string: Parser[StringNode] = "[^\\s'!:(),@$]+".r  ^^ { x => StringNode(x) }
  def t: Parser[BooleanNode]     = "!t"                 ^^^ BooleanNode(true)
  def f: Parser[BooleanNode]     = "!f"                 ^^^ BooleanNode(false)
  def n: Parser[NullNode]        = "!n"                 ^^^ NullNode()
  def quoted: Parser[StringNode] = "'" ~> "((!')|[^'])*".r <~ "'"       ^^ { x => StringNode(unescape(x)) }
  def obj: Parser[ObjectNode]    = "("  ~> repsep(property, ",") <~ ")" ^^ { x => ObjectNode(x) }
  def array: Parser[ArrayNode]   = "!(" ~> repsep(value,    ",") <~ ")" ^^ { x => ArrayNode(x) }
  def value: Parser[ValueNode]   = long | double | string | quoted | t | f | n | obj | array
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

  def parseObject(str: String): ObjectNode = parse("(" + str + ")").asInstanceOf[ObjectNode]
  def parseArray(str: String): ArrayNode = parse("!(" + str + ")").asInstanceOf[ArrayNode]

  private class RisonParseException(message: String) extends RuntimeException(message)

}

