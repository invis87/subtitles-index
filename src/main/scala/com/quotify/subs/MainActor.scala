package com.quotify.subs

import akka.actor.{ActorRefFactory, Actor, Props}
import akka.util.Timeout
import com.quotify.subs.JsonProtocol._
import com.quotify.subs.error.ErrorCode
import com.quotify.subs.error.ServiceExceptions.SubtitlesParsingError
import com.quotify.subs.parser.{Parser, Sub}
import com.quotify.subs.protocol._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{RichSearchHit, ElasticClient, IndexResult, RichSearchResponse}
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, ExceptionHandler, HttpServiceActor}
import spray.util.LoggingContext

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

object MainActor {
  def props(elastic: ElasticClient): Props = Props(new MainActor(elastic))
}

class MainActor(elastic: ElasticClient) extends Actor with MainService {
  override def executionContext: ExecutionContextExecutor = context.dispatcher
  override implicit def actorRefFactory: ActorRefFactory = context

  implicit def myExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      case e: SubtitlesParsingError =>
        log.warning("SubtitlesParsingError")
        complete(ErrorResponse(ErrorCode.PARSING_ERROR, "subtitles parsing error"))
    }

  def receive = runRoute(route)

  override def testConnection: TestConnection = {
    TestConnection("Successfully!")
  }

  override def addMovieSubs(subtitles: SubtitlesEntity): Future[SubtitlesAdded] = {
    val trySubs = Try(Parser.parse(subtitles.subtitles.lines))
    trySubs match {
      case Success(subs) => indexing(subtitles.mediaId, subs)
      case Failure(e) => throw new SubtitlesParsingError(e.getMessage, e)
    }
  }

  private def indexing(mediaId: Int, subs: List[Sub]): Future[SubtitlesAdded] = {
    val indexResults: List[Future[IndexResult]] = subs.map(sub =>
      elastic.execute { index into "movies" / "subtitleType" id indexId(mediaId, sub.number) fields
        ( "mediaId" -> mediaId,
          "from" -> sub.startTime,
          "to" -> sub.endTime,
          "text" -> sub.text)
      })

    def processIndexingResult(results: List[IndexResult]): SubtitlesAdded = {
      val addedSubs = results.filter(res => res.isCreated).map(res => res.getId)
      SubtitlesAdded(mediaId, addedSubs)
    }

    val resSeq: Future[List[IndexResult]] = Future.sequence(indexResults)
    resSeq.map(seq => processIndexingResult(seq))
  }

  private def indexId(mediaId: Int, subNumber: Int): String = {
    s"${mediaId}_$subNumber"
  }

  override def searchSubs(searchEntity: SearchEntity): Future[SubtitlesFind] = {
    val searchResult: Future[RichSearchResponse] = elastic.execute {
      search in "subtitles" query searchEntity.text
    }

    searchResult.map(res => parseSearchRes(res.hits))
  }

  private def parseSearchRes(searchHits: Array[RichSearchHit]): SubtitlesFind = {
    SubtitlesFind(searchHits.map(hit => FindedSub(hit)))
  }
}

trait MainService extends HttpService {

  implicit val timeout = Timeout(5.seconds) //todo: move to config

  def executionContext: ExecutionContextExecutor

  implicit val execContext = executionContext

  def testConnection: TestConnection

  def searchSubs(searchEntity: SearchEntity): Future[SubtitlesFind]
  def addMovieSubs(subtitles: SubtitlesEntity): Future[SubtitlesAdded]

  val route = {
    (get & path("testConnection")) {
      complete {
        testConnection
      }
    } ~
      (post & path("search")) {
        entity(as[SearchEntity]) { search =>
          complete {
            searchSubs(search)
          }
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
