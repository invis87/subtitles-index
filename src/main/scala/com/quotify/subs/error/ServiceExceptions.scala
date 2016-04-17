package com.quotify.subs.error

/**
  * Created by Aleksandrov Vladimir on 17/04/16.
  */
object ServiceExceptions {

  class SubtitlesParsingError(message: String, cause: Throwable) extends RuntimeException(message, cause)

}
