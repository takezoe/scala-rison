package com.github.takezoe.rison

import org.scalatest.FunSuite
import RisonConverter._

class RisonConverterSpec extends FunSuite {

  test("toScala"){
    val node = ObjectNode(Seq(
      PropertyNode(StringNode("name"), StringNode("takezoe")),
      PropertyNode(StringNode("age"), IntNode(39))
    ))

    val map = toScala(node)
    assert(map == Map("name" -> "takezoe", "age" -> 39))
  }

  test("toRison"){
    val map = Map("name" -> "takezoe", "age" -> 39)
    val node = toRison(map)

    assert(node == ObjectNode(List(
      PropertyNode(StringNode("name"), StringNode("takezoe")),
      PropertyNode(StringNode("age"), IntNode(39))
    )))
  }
}
