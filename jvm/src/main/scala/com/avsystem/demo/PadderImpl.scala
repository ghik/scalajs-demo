package com.avsystem.demo

import scala.concurrent.Future

/**
  * Author: ghik
  * Created: 25/04/16.
  */
object PadderImpl extends Padder {
  def leftPad(text: String, width: Int, character: Char): Future[String] =
    Future.successful(character.toString * ((width - text.length) max 0) + text)

  def rightPad(text: String, width: Int, character: Char): Future[String] =
    Future.successful(text + character.toString * ((width - text.length) max 0))
}
