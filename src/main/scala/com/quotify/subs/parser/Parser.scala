package com.quotify.subs.parser

import java.text.SimpleDateFormat

import scala.annotation.tailrec

object Parser {

  val dateFormat = new SimpleDateFormat("hh:mm:ss")

  def parse(strings: Iterator[String]): List[Sub] = {

    @tailrec
    def helper(result: List[Sub]): List[Sub] = {
      val subText = nextSubText(strings)
      if(subText.isEmpty) {
        result
      } else {
        helper(result :+ subFromList(subText))
      }
    }

    helper(List.empty)
  }

  private def nextSubText(strs: Iterator[String]): List[String] = {
    strs.takeWhile(str => str.nonEmpty).toList
  }

  private def subFromList(strs: List[String]): Sub = {
    val number = strs.head.toInt
    val (start :: end :: _) = strs(1).split(" --> ").map(dateFormat.parse).toList
    val text = strs.drop(2) mkString " "
    Sub(number, start, end, text)
  }
}
