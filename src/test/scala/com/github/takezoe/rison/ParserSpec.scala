package com.github.takezoe.rison

import org.scalatest.FunSuite

class ParserSpec extends FunSuite {

  val parser = new Parser {
    def parse(str: String): ValueNode = {
      parse(rison, str) match {
        case Success(matched, _) => matched
        case Failure(msg    , _) => fail(msg)
        case Error  (msg    , _) => fail(msg)
      }
    }
  }

  test("Parser"){
    val obj = parser.parse(
      "(name:'Naoki Takezoe',age:39,programming:!(Java,Scala),languages:(english:!f,japanese:!t))"
    ).asInstanceOf[ObjectNode]

    assert(obj == ObjectNode(List(
      PropertyNode(StringNode("name"), StringNode("Naoki Takezoe")),
      PropertyNode(StringNode("age"), IntNode(39)),
      PropertyNode(StringNode("programming"), ArrayNode(List(
        StringNode("Java"),
        StringNode("Scala")
      ))),
      PropertyNode(StringNode("languages"), ObjectNode(List(
        PropertyNode(StringNode("english"), BooleanNode(false)),
        PropertyNode(StringNode("japanese"), BooleanNode(true))
      )))
    )))



  }

}
