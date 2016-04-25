package com.avsystem.demo

import com.avsystem.commons.rpc.RPCFramework
import upickle.Js

/**
  * Author: ghik
  * Created: 25/04/16.
  */
object UPickleRPC extends RPCFramework {
  type RawValue = Js.Value
  type Reader[T] = upickle.default.Reader[T]
  type Writer[T] = upickle.default.Writer[T]

  def read[T: Reader](rawValue: RawValue): T =
    upickle.default.readJs[T](rawValue)

  def write[T: Writer](value: T): RawValue =
    upickle.default.writeJs[T](value)
}
