package com.github.takezoe.rison

import wvlet.airframe.codec.{MessageCodec, MessageContext}
import wvlet.airframe.msgpack.spi.Value._
import wvlet.airframe.msgpack.spi.{Packer, Unpacker, Value}

object RisonCodec extends MessageCodec[String] {

  override def pack(p: Packer, v: String): Unit = {
    RisonParser.parse(v) match {
      case Right(value) => packRisonValue(p, value)
      case Left(e) => throw new IllegalArgumentException(e)
    }
  }

  private def packRisonValue(p: Packer, v: ValueNode): Unit = {
    v match {
      case ObjectNode(props) =>
        p.packMapHeader(props.size)
        props.foreach { prop =>
          p.packString(prop.key.value)
          packRisonValue(p, prop.value)
        }
      case ArrayNode(array) =>
        p.packArrayHeader(array.length)
        array.foreach { value =>
          packRisonValue(p, value)
        }
      case StringNode(value) =>
        p.packString(value)
      case NullNode() =>
        p.packNil
      case BooleanNode(value) =>
        p.packBoolean(value)
      case DoubleNode(value) =>
        p.packDouble(value)
      case LongNode(value) =>
        p.packLong(value)
    }
  }

  override def unpack(u: Unpacker, v: MessageContext): Unit = {
    val rison = unpackRisonValue(u, u.unpackValue)
    v.setString(rison.toRisonString)
  }

  private def unpackRisonValue(u: Unpacker, v: Value): ValueNode = {
    v match {
      case StringValue(value) =>
        StringNode(value)
      case LongValue(value) =>
        LongNode(value)
      case DoubleValue(value) =>
        DoubleNode(value)
      case BooleanValue(value) =>
        BooleanNode(value)
      case BigIntegerValue(value) =>
        StringNode(value.toString)
      case NilValue =>
        NullNode()
      case ArrayValue(array) =>
        ArrayNode(array.map { value =>
          unpackRisonValue(u, value)
        })
      case MapValue(map) =>
        ObjectNode(map.map{ case (StringValue(key), value) =>
          PropertyNode(StringNode(key), unpackRisonValue(u, value))
        }.toSeq)
    }
  }

}
