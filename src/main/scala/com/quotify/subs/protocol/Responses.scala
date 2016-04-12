package com.quotify.subs.protocol

case class TestConnection(result: String)
case class SubtitlesAdded(mediaId: Int)
case class ErrorResponse(code: Int)