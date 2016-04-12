package com.quotify.subs

import com.quotify.subs.protocol.{ErrorResponse, SubtitlesAdded, SubtitlesEntity, TestConnection}
import spray.json._

object JsonProtocol extends DefaultJsonProtocol {
  implicit val testConnectionFormat = jsonFormat1(TestConnection)
  implicit val errorResponseFormat = jsonFormat1(ErrorResponse)
  implicit val subtitlesAddedFormat = jsonFormat1(SubtitlesAdded)
  implicit val subtitlesEntityFormat = jsonFormat2(SubtitlesEntity)
}
