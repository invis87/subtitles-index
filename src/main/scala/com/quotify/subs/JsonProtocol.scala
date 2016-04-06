package com.quotify.subs

import com.quotify.subs.protocol.TestConnection
import spray.json._

object JsonProtocol extends DefaultJsonProtocol {
  implicit val testConnectionFormat = jsonFormat1(TestConnection)
}
