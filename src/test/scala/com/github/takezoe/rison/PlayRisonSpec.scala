package com.github.takezoe.rison

import org.scalatest.FunSuite
import play.api.libs.json._

case class Resident(name: String, age: Int, role: Option[String])

class PlayRisonSpec extends FunSuite {

  test("PlayRisonSpec.fromPlayJson"){
    val json = Json.parse("""{"name":"Fiver","age":4,"role":null}""")
    val rison = PlayRison.fromPlayJson(json)

    assert(rison.toRisonString == "(name:Fiver,age:4,role:!n)")
  }

  test("PlayRisonSpec.toPlayJson"){
    val rison = RisonParser.parse("(name:Fiver,age:4,role:!n)").right.get
    val json = PlayRison.toPlayJson(rison)

    assert(Json.stringify(json) == """{"name":"Fiver","age":4,"role":null}""")
  }
}
