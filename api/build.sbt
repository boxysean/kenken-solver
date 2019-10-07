import sbt.Keys._
import sbt._

name := "kenkengg"

version := "1.0"

resolvers += Resolver.sonatypeRepo("public")
scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-events" % "2.2.1",
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",
  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "io.spray" %% "spray-json" % "1.3.5",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)
