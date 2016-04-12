package com.quotify.subs.parser

import java.util.Date

case class Sub(number: Int, start: Date, end: Date, text: String) {
  override def toString: String = {
    s"""
       |[$number]
       |[$start] to [$end]
       |$text
     """.stripMargin
  }
}
