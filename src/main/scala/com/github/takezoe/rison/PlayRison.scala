package com.github.takezoe.rison

import play.api.libs.json._

object PlayRison {

  def toPlayJson(node: ValueNode): JsValue = {
    node match {
      case ObjectNode(values) => JsObject(values.map { property =>
        property.key.value -> toPlayJson(property.value)
      })
      case ArrayNode(values)  => JsArray(values.map(toPlayJson))
      case StringNode(value)  => JsString(value)
      case LongNode(value)    => JsNumber(value)
      case DoubleNode(value)  => JsNumber(value)
      case BooleanNode(value) => JsBoolean(value)
      case NullNode()         => JsNull
    }
  }

  def fromPlayJson(node: JsValue): ValueNode = {
    node match {
      case JsObject(values) => ObjectNode(values.map { case (key, value) =>
          PropertyNode(StringNode(key), fromPlayJson(value))
      }.toSeq)
      case JsArray(values)  => ArrayNode(values.map(fromPlayJson))
      case JsString(value)  => StringNode(value)
      case JsNumber(value)  =>
        if(value.isValidLong){
          LongNode(value.toLong)
        } else if(value.isDecimalDouble){
          DoubleNode(value.toDouble)
        } else {
          StringNode(value.toString)
        }
      case JsBoolean(value) => BooleanNode(value)
      case JsNull           => NullNode()
    }
  }

}
