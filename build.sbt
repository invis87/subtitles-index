import Dependencies.Scala

name := "SubtitlesRest"

version := "1.0"

scalaVersion := Dependencies.scalaVersion

libraryDependencies ++= Seq(
  Dependencies.elasticCore, Dependencies.elasticTestkit, Dependencies.slf4jToLog4j, Dependencies.typesafeConfig,
  Dependencies.akka
) ++ Dependencies.Spray.all

dependencyOverrides ++= Scala.all.toSet

cancelable in Global := true