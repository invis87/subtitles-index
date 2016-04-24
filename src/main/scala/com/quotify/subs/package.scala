package com.quotify

import com.quotify.subs.protocol.ErrorResponse

/**
  * Created by Aleksandrov Vladimir on 24/04/16.
  */
package object subs {

  type Response[A] = Either[ErrorResponse, A]

  def OK[A](resp: A): Response[A] = Right(resp)

  def FAIL(resp: ErrorResponse) = Left(resp)
}
