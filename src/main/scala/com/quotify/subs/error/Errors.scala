package com.quotify.subs.error

import com.quotify.subs.protocol.ErrorResponse

/**
  * Created by Aleksandrov Vladimir on 16/04/16.
  */
object Errors {

  private val PARSING_ERROR_CODE = 1
  private val INDEXING_ERROR_CODE = 2

  val PARSING_ERROR = ErrorResponse(PARSING_ERROR_CODE, "subtitles parsing error")
  val INDEXING_ERROR = ErrorResponse(INDEXING_ERROR_CODE, "elasticsearch indexing error")
}