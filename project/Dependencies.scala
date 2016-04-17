import sbt._

object Dependencies {
  val elasticVersion = "2.3.0"
  val scalaVersion = "2.11.8"
  val log4jVersion = "2.5"
  val slf4jVersion = "1.7.20"
  val typesafeVersion = "1.3.0"
  val specs2Version = "3.7"
  val akkaVersion = "2.4.3"
  val logbackVersion = "1.1.7"
  val akkaSlf4jVersion = "2.4.4"
  val scalazVersion = "7.2.2"

  val elasticCore = "com.sksamuel.elastic4s" %% "elastic4s-core" % elasticVersion
  val elasticTestkit = "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elasticVersion % "test"

  val typesafeConfig = "com.typesafe" % "config" % typesafeVersion
  val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaSlf4jVersion

  val specs2 = "org.specs2" %% "specs2-core" % specs2Version % "test"
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val scalaz = "org.scalaz" %% "scalaz-core" % scalazVersion

  object Scala {
    val scalaCompiler = "org.scala-lang" % "scala-compiler" % scalaVersion
    val scalaLibrary = "org.scala-lang" % "scala-library" % scalaVersion
    val scalaReflect = "org.scala-lang" % "scala-reflect" % scalaVersion
    val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.4"

    val all = Seq(scalaCompiler, scalaLibrary, scalaReflect, scalaXml)
  }

  object Spray {
    val sprayVersion = "1.3.3"
    val sprayJsonVersion = "1.3.2"

    val sprayCan = "io.spray" %% "spray-can" % sprayVersion
    val sprayRouting = "io.spray" %% "spray-routing" % sprayVersion
    val sprayJson = "io.spray" %% "spray-json" % sprayJsonVersion
    val sprayTestkit = "io.spray" %% "spray-testkit" % sprayVersion % "test" exclude("org.specs2", "specs2_2.11")

    val all = Seq(sprayCan, sprayRouting, sprayJson, sprayTestkit)
  }

}