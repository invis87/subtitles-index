package com.quotify.subs.protocol


import com.sksamuel.elastic4s.RichSearchHit

case class TestConnection(result: String)

//todo: probably can remove mediaId from that response
case class SubtitlesAdded(mediaId: Int, added: List[String])

case class ErrorResponse(errorCode: Int, description: String)

case class SubtitlesFind(subs: Seq[InternalFindedSub])

case class InternalFindedSub(mediaId: Int, from: String, to: String, text: String)

object FindedSub {

  def apply(hit: RichSearchHit): InternalFindedSub = {
    val map = hit.sourceAsMap
    InternalFindedSub(
      map.get("mediaId").get.asInstanceOf[Int],
      map.get("from").get.asInstanceOf[String],
      map.get("to").get.asInstanceOf[String],
      map.get("text").get.asInstanceOf[String])
  }

}