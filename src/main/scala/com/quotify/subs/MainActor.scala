package com.quotify.subs

import akka.actor.{Actor, ActorRefFactory, Props}
import akka.util.Timeout
import com.quotify.subs.JsonProtocol._
import com.quotify.subs.elastic.Elastic
import com.quotify.subs.error.ErrorCode
import com.quotify.subs.error.ServiceExceptions.SubtitlesParsingError
import com.quotify.subs.parser.{Parser, Sub}
import com.quotify.subs.protocol._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, IndexResult, RichSearchHit, RichSearchResponse}
import org.slf4j.LoggerFactory
import spray.httpx.SprayJsonSupport._
import spray.routing.{ExceptionHandler, HttpService}
import spray.json._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

object MainActor {
  def props(elastic: ElasticClient): Props = Props(new MainActor(elastic))
}

class MainActor(elastic: ElasticClient) extends Actor with MainService {
  override def executionContext: ExecutionContextExecutor = context.dispatcher
  def actorRefFactory: ActorRefFactory = context
  val logger = LoggerFactory.getLogger(this.getClass)

  def receive = runRoute(route)

  override def testConnection: TestConnection = {
    logger.debug("Receive TestConnection request.")
    TestConnection("Successfully!")
  }

  override def addMovieSubs(subtitles: SubtitlesEntity): Future[Response[SubtitlesAdded]] = {
    logger.debug("Receive AddMoviesSubtitles request.\n{}", subtitles.toJson)
    val trySubs = Try(Parser.parse(subtitles.subtitles.lines))
    trySubs match {
      case Success(subs) => indexing(subtitles.mediaId, subs).map(OK)
      case Failure(e) => {
        logger.error("Error while parsing subtitles for mediaId #{}:\n{}", subtitles.mediaId, subtitles.subtitles)
        Future(FAIL(ErrorResponse(ErrorCode.PARSING_ERROR, "subtitles parsing error")))
      }
    }
  }

  private def indexing(mediaId: Int, subs: List[Sub]): Future[SubtitlesAdded] = {
    val startTime =  System.currentTimeMillis
    logger.debug("Start indexing subtitles for mediaId={}", mediaId)
    val indexResults: List[Future[IndexResult]] = subs.map(sub =>
      elastic.execute { index into Elastic.INDEX_NAME / Elastic.TYPE_NAME id indexId(mediaId, sub.number) fields
        ( "mediaId" -> mediaId,
          "from" -> sub.startTime,
          "to" -> sub.endTime,
          "text" -> sub.text)
      })

    def processIndexingResult(results: List[IndexResult]): SubtitlesAdded = {
      logger.debug("Indexing subtitles for mediaId={} took {}s",
        mediaId,
        (System.currentTimeMillis - startTime) / 1000
      )
      val addedSubs = results.filter(res => res.isCreated).map(res => res.getId)
      SubtitlesAdded(mediaId, addedSubs)
    }

    val resSeq: Future[List[IndexResult]] = Future.sequence(indexResults)
    resSeq.map(seq => processIndexingResult(seq))
  }

  private def indexId(mediaId: Int, subNumber: Int): String = {
    s"${mediaId}_$subNumber"
  }

  override def searchSubs(searchEntity: SearchEntity): Future[Response[SubtitlesFind]] = {
    val searchResult: Future[RichSearchResponse] = elastic.execute {
      search in Elastic.INDEX_NAME query searchEntity.text
    }

    searchResult.map(res => {
      logger.debug("Receive Search request for text '{}'. Search took {}s", searchEntity.text, res.tookInMillis/1000)
      OK(parseSearchRes(res.hits))
    })
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

  def searchSubs(searchEntity: SearchEntity): Future[Response[SubtitlesFind]]
  def addMovieSubs(subtitles: SubtitlesEntity): Future[Response[SubtitlesAdded]]

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
