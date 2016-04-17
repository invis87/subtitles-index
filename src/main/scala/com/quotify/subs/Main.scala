package com.quotify.subs

import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.analyzers.{StandardAnalyzer, StopAnalyzer, EnglishLanguageAnalyzer}
import com.sksamuel.elastic4s.mappings.FieldType.{IntegerType, StringType}
import com.sksamuel.elastic4s.{ElasticClient, IndexResult}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask

import scala.util.{Failure, Success}

/**
  * Created by Aleksandrov Vladimir on 03/04/16.
  */
object Main {
  val logger = LoggerFactory.getLogger(this.getClass)

  def elasticTest() = {

    val client = Elastic.client().get
    val resp1 = client.execute { delete index "habrahabr" }.await
    logger.info(s"Delete response: $resp1")

    val resp2: IndexResult = client.execute { index into "habrahabr" / "users" fields "name" -> "Vova" }.await
    logger.info(s"Delete response: $resp2")

    // now we can search for the document we indexed earlier
    val resp = client.execute { search in "habrahabr" / "users" query "Vova" }.await
    logger.info(s"Search response: $resp")
  }

  def main(args: Array[String]): Unit = {
    val clientTry = Elastic.client()

    clientTry match {
      case Success(client) => startApplication(client)
      case Failure(e) => logger.error("Can't connect to Elastic", e)
    }
  }

  private def startApplication(elastic: ElasticClient) = {
    createIndexIfNotExist(elastic)


    implicit val system = ActorSystem("subtitles-system")

    val service = system.actorOf(MainActor.props(elastic), "main-actor")

    implicit val timeout = Timeout(5.seconds) //todo: move to config

    val interface = ConfigFactory.load().getString("spray.interface")
    val port = ConfigFactory.load().getInt("spray.port")
    IO(Http) ? Http.Bind(service, interface, port)
  }

  private def createIndexIfNotExist(elastic: ElasticClient) = {

    import scala.concurrent.ExecutionContext.Implicits._

    val indexExist = elastic.execute(index exists "movies")
    val typeExist = elastic.execute(types exist "subtitleType" in "movies")

    val allFine = for {
      iExist <- indexExist
      tExist <- typeExist
    } yield iExist.isExists && tExist.isExists

    allFine onComplete {
      case Success(res) => if (!res) createIndex(elastic)
      case Failure(e) => throw e
    }
  }

  private def createIndex(elastic: ElasticClient) = {
    logger.debug("Creating index")

    val req = create index "movies" indexSetting ("mapper_dynamic", false) mappings
      mapping("subtitleType").fields(
        "text" typed StringType
      )

    elastic.execute(req)
  }

}