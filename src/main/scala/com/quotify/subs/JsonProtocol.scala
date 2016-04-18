package com.quotify.subs

import com.quotify.subs.protocol._
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scalaz._

object JsonProtocol extends DefaultJsonProtocol {
  implicit val testConnectionFormat = jsonFormat1(TestConnection)

  implicit val errorResponseFormat = jsonFormat2(ErrorResponse)
  implicit val internalFindedSubFormat = jsonFormat4(InternalFindedSub)
  implicit val subtitlesAddedFormat = jsonFormat2(SubtitlesAdded)
  implicit val subtitlesFindFormat = jsonFormat1(SubtitlesFind)
  implicit val searchEntityFormat = jsonFormat1(SearchEntity)
  implicit val subtitlesEntityFormat = jsonFormat2(SubtitlesEntity)

  //don't compile
  implicit class ResponseFWrapper[A : JsonFormat](val res: ResponseF[A]) extends AnyVal {
    def response(implicit exc: ExecutionContext) = res map {
      case \/-(res) => res.toJson
      case -\/(err) => err.toJson
    }
  }

  implicit class ResponseFSubtitlesAddedWrapper(val res: ResponseF[SubtitlesAdded]) extends AnyVal {
    def response(implicit exc: ExecutionContext) = res map {
      case \/-(res) => res.toJson
      case -\/(err) => err.toJson
    }
  }


}
