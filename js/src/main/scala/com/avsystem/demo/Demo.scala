package com.avsystem.demo

import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.{HTMLButtonElement, HTMLInputElement, HTMLSpanElement}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.JSApp
import scala.util.{Failure, Success}

/**
  * Author: ghik
  * Created: 25/04/16.
  */
object Demo extends JSApp {
  def main(): Unit = {
    val strInput = dom.document.getElementById("str").asInstanceOf[HTMLInputElement]
    val widthInput = dom.document.getElementById("width").asInstanceOf[HTMLInputElement]
    val characterInput = dom.document.getElementById("character").asInstanceOf[HTMLInputElement]
    val performButton = dom.document.getElementById("perform").asInstanceOf[HTMLButtonElement]
    val result = dom.document.getElementById("result").asInstanceOf[HTMLSpanElement]

    performButton.onclick = (ev: MouseEvent) => {
      val leftPadder = UPickleRPC.AsRealRPC[Padder].asReal(AjaxRawRPC)

      leftPadder.rightPad(strInput.value, widthInput.valueAsNumber, characterInput.value.charAt(0)).onComplete {
        case Success(value) => result.textContent = value
        case Failure(cause) => dom.window.alert(cause.getMessage)
      }
    }
  }

  private def leftPad(text: String, width: Int, character: Char): String =
    character.toString * ((width - text.length) max 0) + text
}
