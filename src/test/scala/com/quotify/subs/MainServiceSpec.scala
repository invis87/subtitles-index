package com.quotify.subs

import akka.actor.Actor.Receive
import com.quotify.subs.protocol._
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http.StatusCodes

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
        response.status should be equalTo StatusCodes.OK
        responseAs[TestConnection].result === testConnectionResponse
      }
    }
  }

  override def testConnection: TestConnection = TestConnection(testConnectionResponse)

  override def addMovieSubs(subtitles: SubtitlesEntity): Future[Response[SubtitlesAdded]] = {
    Future.successful(
      //todo: this is stub. fix it
      OK(SubtitlesAdded(475634765, List("475634765_1")))
    )
  }



  override def searchSubs(searchEntity: SearchEntity): Future[Response[SubtitlesFind]] = ???

}
