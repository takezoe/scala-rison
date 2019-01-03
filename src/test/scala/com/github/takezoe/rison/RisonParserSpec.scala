package com.github.takezoe.rison

import org.scalatest.FunSuite

class RisonParserSpec extends FunSuite {

  val parser = new RisonParser()

  test("Parser"){
    val obj = parser.parse(
      "(name:'Naoki Takezoe',age:39,programming:!(Java,Scala),languages:(english:!f,japanese:!t))"
    ) match {
      case Right(obj)  => obj.asInstanceOf[ObjectNode]
      case Left(error) => fail(error)
    }

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
