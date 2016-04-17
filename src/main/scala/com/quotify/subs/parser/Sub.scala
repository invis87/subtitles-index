package com.quotify.subs.parser

case class Sub(number: Int, startTime: String, endTime: String, text: String) {
  override def toString: String = {
    s"""
       |[$number]
       |[$startTime] to [$endTime]
       |$text
     """.stripMargin
  }
}
