scala-rison
========

[Rison](https://github.com/Nanonid/rison) parser for Scala

```scala
import com.github.takezoe.rison._

val parser = new RisonParser()

// parse
parser.parse("(name:takezoe,age:39)") match {
  case Right(node) => println(node.toScala) // => Map(name -> takezoe, age -> 39)
  case Left(error) => println(error)
}

// convert from Scala's Map
val node: RisonNode = RisonNode.fromScala(Map("name" -> "Naoki Takezoe", "age" -> 39))
println(node.toRisonString) // => (name:'Naoki Takezoe',age:39)

// URL encode
val encoded: String = node.toUrlEncodedString
println(encoded) // => (name:'Naoki+Takezoe',email:'takezoe@gmail.com')"

// o-rison
val orison: ObjectNode = parser.parseObject("name:takezoe,age:39")
println(orison.toObjectString) // => name:takezoe,age:39

// a-rison
val arison: ArrayNode = parser.parseArray("Java,Scala")
println(arison.toArrayString) // => Java,Scala
```