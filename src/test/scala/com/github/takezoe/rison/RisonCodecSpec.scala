package com.github.takezoe.rison

import org.scalatest.funsuite.AnyFunSuite
import wvlet.airframe.codec.MessageCodecFactory

object RisonCodecSpec {
  case class Player(name: String, number: Int, position: Seq[String])
}

class RisonCodecSpec extends AnyFunSuite {
  import RisonCodecSpec._

  test("pack"){
    val bytes = RisonCodec.toMsgPack("(name:Ozil,number:10,position:!(FW,MF))")

    val mapCodecFactory = MessageCodecFactory.defaultFactory.withObjectMapCodec
    val codec = mapCodecFactory.of[Player]
    val player = codec.unpackBytes(bytes)

    assert(player == Some(Player("Ozil", 10, Seq("FW", "MF"))))
  }

  test("unpack"){
    val player = Player("Ozil", 10, Seq("FW", "MF"))

    val mapCodecFactory = MessageCodecFactory.defaultFactory.withObjectMapCodec
    val codec = mapCodecFactory.of[Player]
    val bytes = codec.toMsgPack(player)

    val rison = RisonCodec.unpackBytes(bytes)
    assert(rison == Some("(name:Ozil,number:10,position:!(FW,MF))"))
  }
}
