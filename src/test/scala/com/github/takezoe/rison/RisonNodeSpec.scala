package com.github.takezoe.rison

import org.scalatest.FunSuite

class RisonNodeSpec extends FunSuite {

  test("toScala"){
    val node = ObjectNode(Seq(
      PropertyNode(StringNode("name"), StringNode("takezoe")),
      PropertyNode(StringNode("age"), IntNode(39))
    ))

    val map = node.toScala
    assert(map == Map("name" -> "takezoe", "age" -> 39))
  }

  test("toRison"){
    val map = Map("name" -> "takezoe", "age" -> 39)
    val node = RisonNode.fromScala(map)

    assert(node == ObjectNode(List(
      PropertyNode(StringNode("name"), StringNode("takezoe")),
      PropertyNode(StringNode("age"), IntNode(39))
    )))
  }

  test("toString"){
    val node = ObjectNode(Seq(
      PropertyNode(StringNode("name"), StringNode("Naoki Takezoe")),
      PropertyNode(StringNode("age"), IntNode(39))
    ))
    val rison = node.toRisonString

    assert(rison == "(name:'Naoki Takezoe',age:39)")
  }
}
