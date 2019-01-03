scala-rison
========

[Rison](https://github.com/Nanonid/rison) parser for Scala

```scala
import com.github.takezoe.rison._

val parser = new RisonParser()

// parse
parser.parse("(name:Lacazette,age:27)") match {
  case Right(node) => println(node.toScala) // => Map(name -> Lacazette, age -> 27)
  case Left(error) => println(error)
}

// convert from Scala's Map
val node: RisonNode = RisonNode.fromScala(Map("name" -> "Alexandre Lacazette", "twitter" -> "@LacazetteAlex"))
println(node.toRisonString) // => (name:'Alexandre Lacazette',twitter:'@LacazetteAlex')

// URL encode
val encoded: String = node.toUrlEncodedString
println(encoded) // => (name:'Alexandre+Lacazette',twitter:'@LacazetteAlex')

// o-rison
val orison: ObjectNode = parser.parseObject("name:Lacazette,age:27")
println(orison.toObjectString) // => name:Lacazette,age:27

// a-rison
val arison: ArrayNode = parser.parseArray("Lacazette,Aubameyang,Ozil")
println(arison.toArrayString) // => Lacazette,Aubameyang,Ozil
```