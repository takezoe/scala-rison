scala-rison [![Build Status](https://travis-ci.org/takezoe/scala-rison.svg?branch=master)](https://travis-ci.org/takezoe/scala-rison) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.takezoe/scala-rison_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.takezoe/scala-rison_2.12) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)
========

[Rison](https://github.com/Nanonid/rison) parser for Scala

## Installation

```scala
libraryDependencies += "com.github.takezoe" %% "scala-rison" % "0.0.2"
```

## Usage


```scala
import com.github.takezoe.rison._

val parser = new RisonParser()

// case class
case class Player(name: String, age: Int)

// parse
parser.parse("(name:Lacazette,age:27)") match {
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
val node2: RisonNode = RisonNode.fromScala(Map("name" -> "Alexandre Lacazette", "twitter" -> "@LacazetteAlex"))
println(node2.toRisonString) // => (name:'Alexandre Lacazette',twitter:'@LacazetteAlex')

// URL encode
val encoded: String = node2.toUrlEncodedString
println(encoded) // => (name:'Alexandre+Lacazette',twitter:'@LacazetteAlex')

// o-rison
val orison: ObjectNode = parser.parseObject("name:Lacazette,age:27")
println(orison.toObjectString) // => name:Lacazette,age:27

// a-rison
val arison: ArrayNode = parser.parseArray("Lacazette,Aubameyang,Ozil")
println(arison.toArrayString) // => Lacazette,Aubameyang,Ozil
```