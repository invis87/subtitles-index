package com.quotify.subs

import com.quotify.subs.protocol._
import spray.json._

import scala.concurrent.Future
import scalaz._

object JsonProtocol extends DefaultJsonProtocol {
  implicit val testConnectionFormat = jsonFormat1(TestConnection)

  implicit val errorResponseFormat = jsonFormat2(ErrorResponse)
  implicit val internalFindedSubFormat = jsonFormat4(InternalFindedSub)
  implicit val subtitlesAddedFormat = jsonFormat2(SubtitlesAdded)
  implicit val subtitlesFindFormat = jsonFormat1(SubtitlesFind)
  implicit val searchEntityFormat = jsonFormat1(SearchEntity)
  implicit val subtitlesEntityFormat = jsonFormat2(SubtitlesEntity)

  implicit val eitherTSubtitlesFindFormat = new JsonFormat[(ErrorResponse \/ SubtitlesFind)] {
    def write(either: (ErrorResponse \/ SubtitlesFind)) = either match {
      case -\/(a)  => a.toJson
      case \/-(a) => a.toJson
    }
    def read(value: JsValue) = deserializationError("Impossible to deserialize to EitherT")
  }

}
