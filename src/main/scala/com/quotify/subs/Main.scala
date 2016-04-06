package com.quotify.subs

import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import com.quotify.subs.actors.MainActor
import com.sksamuel.elastic4s.ElasticDsl._
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
    client.java
    val resp1 = client.execute { delete index "habrahabr" }.await
    logger.info(s"Delete response: $resp1")

    // now we can search for the document we indexed earlier
    val resp = client.execute { search in "habrahabr" / "users" query "Vova" }.await
    logger.info(s"Search response: $resp")
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("subtitles-system")

    val service = system.actorOf(MainActor.props(), "main-actor")

    implicit val timeout = Timeout(5.seconds)

    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  }
}
