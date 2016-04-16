package com.quotify.subs

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.quotify.subs.parser.Parser
import com.quotify.subs.protocol.{ErrorResponse, SubtitlesAdded, TestConnection, SubtitlesEntity}
import com.sksamuel.elastic4s.{IndexResult, ElasticClient}
import spray.routing.HttpService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import com.sksamuel.elastic4s.ElasticDsl._

import scala.util.{Failure, Success}

object MainActor {
  def props(elastic: ElasticClient): Props = Props(new MainActor(elastic))
}

class MainActor(elastic: ElasticClient) extends Actor with MainService {
  override def executionContext: ExecutionContextExecutor = context.dispatcher

  def actorRefFactory = context

  def receive = runRoute(route)

  override def testConnection: TestConnection = {
    TestConnection("Successfully!")
  }

  override def addMovieSubs(subtitles: SubtitlesEntity): Future[Response[SubtitlesAdded]] = {
    val subs = Parser.parse(subtitles.subtitles.lines)

    val indexResults: List[Future[IndexResult]] = subs.map(sub =>
      elastic.execute { index into "movies" / "subtitles" fields
        ("id" -> subtitles.mediaId, //todo: избыточность! (в каждом индексе есть mediaId
        "from" -> sub.start,
        "to" -> sub.end,
        "text" -> sub.text)
      })


    //todo: rewrite
    val xx: Future[List[IndexResult]] = Future.sequence(indexResults)

//    xx onComplete {
//      case Success(indexes) => Right(SubtitlesAdded(subtitles.mediaId))
//      case Failure(e) => Left(ErrorResponse(555))
//    }

    xx.map(indexes =>
      if(indexes.forall(result => result.isCreated))
        Right(SubtitlesAdded(subtitles.mediaId))
    else
        Left(ErrorResponse(555))
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
