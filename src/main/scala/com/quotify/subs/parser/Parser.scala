package com.quotify.subs.parser

import scala.annotation.tailrec

object Parser {

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
    val (start :: end :: _) = strs(1).split(" --> ").toList
    val text = strs.drop(2) mkString " "
    Sub(number, start, end, text)
  }
}
