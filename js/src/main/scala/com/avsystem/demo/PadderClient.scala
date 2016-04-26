package com.avsystem.demo

import com.avsystem.demo.UPickleRPC._
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.{HTMLButtonElement, HTMLInputElement, HTMLSpanElement}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.JSApp
import scala.util.{Failure, Success}

object PadderClient extends JSApp {
  def main(): Unit = {
    val strInput = dom.document.getElementById("str").asInstanceOf[HTMLInputElement]
    val widthInput = dom.document.getElementById("width").asInstanceOf[HTMLInputElement]
    val characterInput = dom.document.getElementById("character").asInstanceOf[HTMLInputElement]
    val performButton = dom.document.getElementById("perform").asInstanceOf[HTMLButtonElement]
    val result = dom.document.getElementById("result").asInstanceOf[HTMLSpanElement]

    performButton.onclick = (ev: MouseEvent) => {
      val text = strInput.value
      val width = widthInput.valueAsNumber
      val char = characterInput.value.charAt(0)

      result.textContent = leftPad(text, width, char)
    }

    def leftPad(text: String, width: Int, character: Char): String =
      character.toString * ((width - text.length) max 0) + text

    def consultPadderServer(): Unit = {
      val padder: Padder = AsRealRPC[Padder].asReal(AjaxRawRPC)
      result.textContent = "computing..."

      val text = strInput.value
      val width = widthInput.valueAsNumber
      val char = characterInput.value.charAt(0)

      padder.leftPad(text, width, char).onComplete {
        case Success(value) => result.textContent = value
        case Failure(cause) => dom.window.alert(cause.getMessage)
      }
    }
  }
}
