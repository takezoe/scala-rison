scala-rison
========

Rison parser for Scala

```scala
import com.github.takezoe.rison._

val parser = new RisonParser()
parser.parse("(name:takezoe,age:39)") match {
  case Right(obj)  => println(obj.toScala)
  case Left(error) => println(error)
}

// => Map(name -> takezoe, age -> 39)
```