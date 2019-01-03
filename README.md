scala-rison
========

[Rison](https://github.com/Nanonid/rison) parser for Scala

```scala
import com.github.takezoe.rison._

val parser = new RisonParser()

parser.parse("(name:takezoe,age:39)") match {
  case Right(obj)  => println(obj.toScala)
  case Left(error) => println(error)
}
// => Map(name -> takezoe, age -> 39)

val node = RisonNode.fromScala(Map("name" -> "Naoki Takezoe", "age" -> 39))
println(node.toRisonString)
// => (name:'Naoki Takezoe',age:39)

val encoded = node.toUrlEncodedString
println(encoded)
// => (name:'Naoki+Takezoe',email:'takezoe@gmail.com')"
```