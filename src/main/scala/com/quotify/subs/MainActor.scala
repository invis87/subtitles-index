package com.quotify.subs

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.quotify.subs.protocol.TestConnection
import spray.routing.HttpService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object MainActor {
  def props(): Props = Props(new MainActor())
}

class MainActor extends Actor with MainService {
  override def executionContext: ExecutionContextExecutor = context.dispatcher
  def actorRefFactory = context

  def receive = runRoute(route)

  override def testConnection: Future[TestConnection] = {
    Future.successful(TestConnection("Successfully!"))
  }

}

trait MainService extends HttpService {
  import spray.httpx.SprayJsonSupport._
  import com.quotify.subs.JsonProtocol._

  implicit val timeout = Timeout(5.seconds)
  def executionContext: ExecutionContextExecutor
  implicit val execContext = executionContext

  def testConnection: Future[TestConnection]

  val route = {
    (get & path("testConnection")) {
      complete {
        testConnection
      }
    }
  }
}
