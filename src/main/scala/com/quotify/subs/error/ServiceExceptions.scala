package com.quotify.subs.error

/**
  * Created by Aleksandrov Vladimir on 17/04/16.
  */
object ServiceExceptions {

  class ParseError(cause: Throwable) extends Exception(cause)
  class SubtitlesParsingError(message: String, cause: Throwable) extends RuntimeException(message, cause)

}
