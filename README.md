scala-rison [![Build Status](https://travis-ci.org/takezoe/scala-rison.svg?branch=master)](https://travis-ci.org/takezoe/scala-rison) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.takezoe/scala-rison_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.takezoe/scala-rison_2.12) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)
========

[Rison](https://github.com/Nanonid/rison) parser for Scala

## Installation

```scala
libraryDependencies += "com.github.takezoe" %% "scala-rison" % "0.0.1"
```

## Usage


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