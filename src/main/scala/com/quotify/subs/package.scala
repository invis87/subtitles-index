package com.quotify

import com.quotify.subs.protocol.ErrorResponse

import scala.concurrent.Future
import scalaz.{EitherT, \/}
import scalaz.Scalaz._

/**
  * Created by Aleksandrov Vladimir on 17/04/16.
  */
package object subs {

  type Response[JsonResponse]  = ErrorResponse \/ JsonResponse
  type ResponseF[JsonResponse] = EitherT[Future, ErrorResponse, JsonResponse]

  def ok[A](res: A): Response[A] = res.right
  def fail[A](f: ErrorResponse): Response[A] = f.left
}
