name := "scala-rison"

organization := "com.github.takezoe"

version := "0.0.3"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
  "org.wvlet.airframe" %% "airframe-surface" % "0.79",
  "org.wvlet.airframe" %% "airframe-codec" % "0.79" % "optional",
  "com.typesafe.play" %% "play-json" % "2.6.10" % "optional",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

scalacOptions := Seq("-deprecation", "-feature")

pomExtra := (
  <url>https://github.com/takezoe/scala-rison</url>
  <licenses>
    <license>
      <name>The Apache Software License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/takezoe/scala-rison</url>
    <connection>scm:git:https://github.com/takezoe/scala-rison.git</connection>
  </scm>
  <developers>
    <developer>
      <id>takezoe</id>
      <name>Naoki Takezoe</name>
    </developer>
  </developers>
)
