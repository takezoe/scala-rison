scala-rison
========

Rison parser for Scala

```scala
import com.github.takezoe.rison._

val parser = new RisonParser()
parser.parse("(name:takezoe,age:39)") match {
  case Right(obj)  => println(obj)
  case Left(error) => println(error)
}

// => ObjectNode(List(PropertyNode(StringNode(name),StringNode(takezoe)), PropertyNode(StringNode(age),IntNode(39)))
```