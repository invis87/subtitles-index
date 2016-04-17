import Dependencies.Scala

name := "SubtitlesRest"

version := "1.0"

scalaVersion := Dependencies.scalaVersion

libraryDependencies ++= Seq(
  Dependencies.elasticCore, Dependencies.elasticTestkit, Dependencies.logback, Dependencies.typesafeConfig,
  Dependencies.akka, Dependencies.specs2, Dependencies.akkaSlf4j, Dependencies.scalaz
) ++ Dependencies.Spray.all

dependencyOverrides ++= Scala.all.toSet

fork in run := true

cancelable in Global := true

assemblyMergeStrategy in assembly := {
  //elastic4s-core have different BaseDateTime.class
  case PathList("org","joda","time","base", "BaseDateTime.class") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}