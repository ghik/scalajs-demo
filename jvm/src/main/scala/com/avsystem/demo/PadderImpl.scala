package com.avsystem.demo

import java.util.concurrent.Executors

import com.avsystem.commons.jiop.JavaInterop._

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

/**
  * Author: ghik
  * Created: 25/04/16.
  */
object PadderImpl extends Padder {
  def leftPad(text: String, width: Int, character: Char): Future[String] = {
    val result = character.toString * ((width - text.length) max 0) + text
    delay(result, 2.seconds)
  }

  def rightPad(text: String, width: Int, character: Char): Future[String] = {
    val result = text + character.toString * ((width - text.length) max 0)
    delay(result, 2.seconds)
  }

  private val scheduler = Executors.newSingleThreadScheduledExecutor

  private def delay[T](value: T, after: Duration): Future[T] = {
    val promise = Promise[T]()
    scheduler.schedule(jRunnable(promise.success(value)), after.length, after.unit)
    promise.future
  }
}
