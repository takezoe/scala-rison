package com.github.takezoe.rison

import org.scalatest.FunSuite

class RisonParserSpec extends FunSuite {

  val parser = new RisonParser()

  test("Parser"){
    val obj = parser.parse(
      "(name:'Naoki Takezoe',age:39,programming:!((name:Java,ratio:0.2),(name:Scala,ratio:0.8)),languages:(english:!f,japanese:!t))"
    ) match {
      case Right(obj)  => obj.asInstanceOf[ObjectNode]
      case Left(error) => fail(error)
    }

    assert(obj == ObjectNode(List(
      PropertyNode(StringNode("name"), StringNode("Naoki Takezoe")),
      PropertyNode(StringNode("age"), LongNode(39)),
      PropertyNode(StringNode("programming"), ArrayNode(List(
        ObjectNode(List(
          PropertyNode(StringNode("name"), StringNode("Java")),
          PropertyNode(StringNode("ratio"), DoubleNode(0.2))
        )),
        ObjectNode(List(
          PropertyNode(StringNode("name"), StringNode("Scala")),
          PropertyNode(StringNode("ratio"), DoubleNode(0.8))
        ))
      ))),
      PropertyNode(StringNode("languages"), ObjectNode(List(
        PropertyNode(StringNode("english"), BooleanNode(false)),
        PropertyNode(StringNode("japanese"), BooleanNode(true))
      )))
    )))
  }

}
