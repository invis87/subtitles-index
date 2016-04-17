package com.quotify.subs.parser

import com.quotify.subs.error.ServiceExceptions.ParseError

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object Parser {

  def parse(strings: Iterator[String]): List[Sub] = {

    @tailrec
    @throws(classOf[ParseError])
    def helper(result: List[Sub]): List[Sub] = {
      val subText = nextSubText(strings)
      if(subText.isEmpty) {
        result
      } else {
        helper(result :+ subFromList(subText))
      }
    }

    val result = Try(helper(List.empty))
    result match {
      case Success(res) => res
      case Failure(e) => throw new ParseError(e)
    }
  }

  private def nextSubText(strs: Iterator[String]): List[String] = {
    strs.takeWhile(str => str.nonEmpty).toList
  }

  private def subFromList(strs: List[String]): Sub = {
    val number = strs.head.toInt
    val (start :: end :: _) = strs(1).split(" --> ").toList
    val text = strs.drop(2) mkString " "
    Sub(number, start, end, text)
  }
}
