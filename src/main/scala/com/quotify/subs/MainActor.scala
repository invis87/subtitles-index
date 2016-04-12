package com.quotify.subs

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.quotify.subs.protocol.{ErrorResponse, SubtitlesAdded, TestConnection, SubtitlesEntity}
import spray.routing.HttpService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object MainActor {
  def props(): Props = Props(new MainActor())
}

class MainActor extends Actor with MainService {
  override def executionContext: ExecutionContextExecutor = context.dispatcher

  def actorRefFactory = context

  def receive = runRoute(route)

  override def testConnection: TestConnection = {
    TestConnection("Successfully!")
  }

  override def addMovieSubs(subtitles: SubtitlesEntity): Future[Response[SubtitlesAdded]] = {
    Future.successful(
    //todo: this is stub. fix it
      Right(SubtitlesAdded(475634765))
    )
  }
}

trait MainService extends HttpService {

  import com.quotify.subs.JsonProtocol._
  import spray.httpx.SprayJsonSupport._

  implicit val timeout = Timeout(5.seconds)

  def executionContext: ExecutionContextExecutor

  implicit val execContext = executionContext

  def testConnection: TestConnection

  def addMovieSubs(subtitles: SubtitlesEntity): Future[Response[SubtitlesAdded]]

  val route = {
    (get & path("testConnection")) {
      complete {
        testConnection
      }
    } ~
      (post & path("addMovieSubs")) {
        entity(as[SubtitlesEntity]) { subtitles =>
          complete {
            addMovieSubs(subtitles)
          }
        }
      }
  }
}
