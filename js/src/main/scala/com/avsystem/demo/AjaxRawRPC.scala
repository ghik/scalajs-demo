package com.avsystem.demo

import com.avsystem.demo.UPickleRPC._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData
import upickle.Js

import scala.concurrent.Future

/**
  * Author: ghik
  * Created: 25/04/16.
  */
object AjaxRawRPC extends RawRPC {

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  private def handleAjax(input: String): Future[String] =
    Ajax.post("http://localhost:8080", InputData.str2ajax(input)).map(_.responseText)

  private def rawInvocationToJS(rpcName: String, argLists: List[List[Js.Value]]): Js.Value =
    Js.Obj("rpcName" -> Js.Str(rpcName), "argLists" -> Js.Arr(argLists.map(args => Js.Arr(args: _*)): _*))

  def call(rpcName: String, argLists: List[List[Js.Value]]): Future[Js.Value] = {
    val json = upickle.json.write(rawInvocationToJS(rpcName, argLists))
    handleAjax(json).map { response =>
      upickle.json.read(response) match {
        case Js.Obj(("success", response: Js.Value)) => response
        case Js.Obj(("failure", Js.Str(failureMsg))) =>
          throw new Exception(failureMsg)
      }
    }
  }

  def fire(rpcName: String, argLists: List[List[Js.Value]]) =
    throw new UnsupportedOperationException

  def get(rpcName: String, argLists: List[List[Js.Value]]) =
    throw new UnsupportedOperationException
}
