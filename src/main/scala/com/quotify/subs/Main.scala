package com.quotify.subs

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.quotify.subs.elastic.Elastic
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import spray.can.Http

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/**
  * Created by Aleksandrov Vladimir on 03/04/16.
  */
object Main {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val clientTry = Try {
      Elastic.createMoviesIndexIfNotExist()
      Elastic.client
    }

    clientTry match {
      case Success(client) => startApplication(client)
      case Failure(e) => logger.error("Can't connect to Elastic. Stopping.", e)
    }
  }

  private def startApplication(elastic: ElasticClient) = {
    implicit val system = ActorSystem("subtitles-system")

    val service = system.actorOf(MainActor.props(elastic), "main-actor")

    implicit val timeout = Timeout(5.seconds) //todo: move to config

    val interface = ConfigFactory.load().getString("spray.interface")
    val port = ConfigFactory.load().getInt("spray.port")
    IO(Http) ? Http.Bind(service, interface, port)
  }

}