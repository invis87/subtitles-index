package com.quotify

import com.quotify.subs.protocol.ErrorResponse

package object subs {
  type Response[T] = Either[ErrorResponse, T]
}
