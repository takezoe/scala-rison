package com.github.takezoe.rison

import java.net.URLDecoder

import org.scalatest.FunSuite

class RisonNodeSpec extends FunSuite {

  test("toScala"){
    val node = ObjectNode(Seq(
      PropertyNode(StringNode("name"), StringNode("Lacazette")),
      PropertyNode(StringNode("age"), LongNode(27))
    ))

    val map = node.toScala
    assert(map == Map("name" -> "Lacazette", "age" -> 27))
  }

  case class Player(name: String, age: Int)

  test("toRison"){
    // from Map
    val map = Map("name" -> "Lacazette", "age" -> 27)
    val node1 = RisonNode.fromScala(map)

    assert(node1 == ObjectNode(List(
      PropertyNode(StringNode("name"), StringNode("Lacazette")),
      PropertyNode(StringNode("age"), LongNode(27))
    )))

    // from Case class
    val obj = Player("Lacazette", 27)
    val node2 = RisonNode.fromScala(obj)

    assert(node2 == ObjectNode(List(
      PropertyNode(StringNode("name"), StringNode("Lacazette")),
      PropertyNode(StringNode("age"), LongNode(27))
    )))
  }

  test("toRisonString"){
    val node = ObjectNode(Seq(
      PropertyNode(StringNode("name"), StringNode("Alexandre Lacazette")),
      PropertyNode(StringNode("age"), LongNode(27))
    ))
    val rison = node.toRisonString

    assert(rison == "(name:'Alexandre Lacazette',age:27)")
  }

  test("toUrlEncodedString"){
    val node = RisonNode.fromScala(Map("name" -> "Alexandre Lacazette", "twitter" -> "@LacazetteAlex"))
    val encoded = node.toUrlEncodedString
    assert(encoded == "(name:'Alexandre+Lacazette',twitter:'@LacazetteAlex')")
  }

  test("urlDecode"){
    val encoded = "(name:'Alexandre+Lacazette',twitter:'@LacazetteAlex')"
    val decoded = URLDecoder.decode(encoded, "UTF-8")

    val parser = new RisonParser()
    parser.parse(decoded) match {
      case Right(node) =>
        assert(node == ObjectNode(List(
          PropertyNode(StringNode("name"), StringNode("Alexandre Lacazette")),
          PropertyNode(StringNode("twitter"), StringNode("@LacazetteAlex"))
        )))
      case Left(error) =>
        fail(error)
    }
  }

}
