package com.quotify.subs

import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.IndexResult
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask

/**
  * Created by Aleksandrov Vladimir on 03/04/16.
  */
object Main {
  val logger = LoggerFactory.getLogger(this.getClass)

  def elasticTest() = {

    val client = Elastic.client()
    val resp1 = client.execute { delete index "habrahabr" }.await
    logger.info(s"Delete response: $resp1")

    val resp2: IndexResult = client.execute { index into "habrahabr" / "users" fields "name" -> "Vova" }.await
    logger.info(s"Delete response: $resp2")

    // now we can search for the document we indexed earlier
    val resp = client.execute { search in "habrahabr" / "users" query "Vova" }.await
    logger.info(s"Search response: $resp")
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("subtitles-system")

    val client = Elastic.client()

    val service = system.actorOf(MainActor.props(client), "main-actor")

    implicit val timeout = Timeout(5.seconds)

    val interface = ConfigFactory.load().getString("spray.interface")
    val port = ConfigFactory.load().getInt("spray.port")
    IO(Http) ? Http.Bind(service, interface, port)
  }
}