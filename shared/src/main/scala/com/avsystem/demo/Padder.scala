package com.avsystem.demo

import com.avsystem.commons.rpc.RPC

import scala.concurrent.Future

/**
  * Author: ghik
  * Created: 25/04/16.
  */
@RPC
trait Padder {
  def leftPad(text: String, width: Int, character: Char): Future[String]

  def rightPad(text: String, width: Int, character: Char): Future[String]
}
