package com.github.takezoe.rison

import java.net.URLDecoder

import org.scalatest.FunSuite

// TODO Inner class isn't supported by jackson-scala-module.
case class Player(name: String, age: Int)

class RisonNodeSpec extends FunSuite {

  test("case class"){
    val node = RisonNode.fromScala(Player("Lacazette", 27))

    assert(node == ObjectNode(List(
      PropertyNode(StringNode("name"), StringNode("Lacazette")),
      PropertyNode(StringNode("age"), LongNode(27))
    )))

    val player = node.asInstanceOf[ObjectNode].to[Player]
    assert(player == Player("Lacazette", 27))
  }

  test("toScala"){
    val node = ObjectNode(Seq(
      PropertyNode(StringNode("name"), StringNode("Lacazette")),
      PropertyNode(StringNode("age"), LongNode(27))
    ))

    val map = node.toScala
    assert(map == Map("name" -> "Lacazette", "age" -> 27))
  }

  test("toRison"){
    val map = Map("name" -> "Lacazette", "age" -> 27)
    val node = RisonNode.fromScala(map)

    assert(node == ObjectNode(List(
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
