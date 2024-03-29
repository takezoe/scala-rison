scala-rison [![Scala CI](https://github.com/takezoe/scala-rison/actions/workflows/scala.yml/badge.svg)](https://github.com/takezoe/scala-rison/actions/workflows/scala.yml) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.takezoe/scala-rison_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.takezoe/scala-rison_2.12) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/takezoe/scala-rison/blob/master/LICENSE)
========

[Rison](https://github.com/Nanonid/rison) parser for Scala

## Installation

```scala
libraryDependencies += "com.github.takezoe" %% "scala-rison" % "0.0.4"
```

## Usage


```scala
import com.github.takezoe.rison._

// case class
case class Player(name: String, age: Int)

// parse
RisonParser.parse("(name:Lacazette,age:27)") match {
  case Right(node) => {
    // to Scala Map
    println(node.toScala) // => Map(name -> Lacazette, age -> 27)
    // to Case class
    println(node.asInstanceOf[ObjectNode].to[Player]) // => Player(Lacazette, 27)
  }
  case Left(error) => println(error)
}

// convert from Case class
val node1: RisonNode = RisonNode.fromScala(Player("Lacazette", 27))
println(node1.toRisonString) // => (name:Lacazette',age:27)

// convert from Scala Map
val node2: RisonNode = RisonNode.fromScala(
  Map("name" -> "Alexandre Lacazette", "twitter" -> "@LacazetteAlex")
)
println(node2.toRisonString) // => (name:'Alexandre Lacazette',twitter:'@LacazetteAlex')

// URL encode
val encoded: String = node2.toUrlEncodedString
println(encoded) // => (name:'Alexandre+Lacazette',twitter:'@LacazetteAlex')

// o-rison
val orison: ObjectNode = RisonParser.parseObject("name:Lacazette,age:27")
println(orison.toObjectString) // => name:Lacazette,age:27

// a-rison
val arison: ArrayNode = RisonParser.parseArray("Lacazette,Aubameyang,Ozil")
println(arison.toArrayString) // => Lacazette,Aubameyang,Ozil
```

## Play JSON integration

By using [play-json](https://github.com/playframework/play-json) with scala-rison, you can convert Rison and Json each other and also use play-json's the ability of type-safe case class conversion.

You need to add following play-json dependency additionally to use pla-json integration:

```scala
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.10"
```


```scala
// Json to Rison
val json1 = Json.parse("""{"name":"Lacazette","age":27}""")
val rison1 = PlayRison.fromPlayJson(json1)
println(rison1) // => (name:Lacazette,age:27)

// Rison to Json
val rison2 = RisonParser.parse("(name:Lacazette,age:27)").right.get
val json2 = PlayRison.toPlayJson(rison2)
println(json2) // => {"name":"Lacazette","age":27}
```
