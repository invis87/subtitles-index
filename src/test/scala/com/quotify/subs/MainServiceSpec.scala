package com.quotify.subs

import com.quotify.subs.protocol.TestConnection
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http.StatusCodes._

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by Aleksandrov Vladimir on 09/04/16.
  */
class MainServiceSpec extends Specification with Specs2RouteTest with MainService {

  import spray.httpx.SprayJsonSupport._
  import com.quotify.subs.JsonProtocol._

  override def executionContext: ExecutionContextExecutor = system.dispatcher

  def actorRefFactory = system

  val testConnectionResponse = "Successfully!"

  "MainService" should {

    "should return OK on testConnection" in {
      Get("/testConnection") ~> route ~> check {
        handled === true
        response.status should be equalTo OK
        responseAs[TestConnection].result === testConnectionResponse
      }
    }
  }

  override def testConnection: Future[TestConnection] = Future.successful(TestConnection(testConnectionResponse))

}
