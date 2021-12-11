name := "scala-rison"

organization := "com.github.takezoe"

version := "0.0.4"

scalaVersion := "2.13.7"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.0",
  "org.wvlet.airframe" %% "airframe-surface" % "21.12.0",
  "org.wvlet.airframe" %% "airframe-codec" % "21.12.0" % "optional",
  "com.typesafe.play" %% "play-json" % "2.9.2" % "optional",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test"
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

scalacOptions := Seq("-deprecation", "-feature", "-target:11")

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
