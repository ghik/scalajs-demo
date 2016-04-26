package com.avsystem.demo

import com.avsystem.commons.rpc.RPC

import scala.concurrent.Future

@RPC trait Padder {
  def leftPad(text: String, width: Int, character: Char): Future[String]
}
