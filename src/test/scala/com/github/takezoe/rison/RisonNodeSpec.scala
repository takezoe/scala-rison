package com.github.takezoe.rison

import java.net.URLDecoder

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

  test("toRisonString"){
    val node = ObjectNode(Seq(
      PropertyNode(StringNode("name"), StringNode("Naoki Takezoe")),
      PropertyNode(StringNode("age"), IntNode(39))
    ))
    val rison = node.toRisonString

    assert(rison == "(name:'Naoki Takezoe',age:39)")
  }

  test("toUrlEncodedString"){
    val node = RisonNode.fromScala(Map("name" -> "Naoki Takezoe", "email" -> "takezoe@gmail.com"))
    val encoded = node.toUrlEncodedString
    assert(encoded == "(name:'Naoki+Takezoe',email:'takezoe@gmail.com')")
  }

  test("urlDecode"){
    val encoded = "(name:'Naoki+Takezoe',email:'takezoe@gmail.com')"
    val decoded = URLDecoder.decode(encoded, "UTF-8")

    val parser = new RisonParser()
    parser.parse(decoded) match {
      case Right(node) =>
        assert(node == ObjectNode(List(
          PropertyNode(StringNode("name"), StringNode("Naoki Takezoe")),
          PropertyNode(StringNode("email"), StringNode("takezoe@gmail.com"))
        )))
      case Left(error) =>
        fail(error)
    }
  }

}
