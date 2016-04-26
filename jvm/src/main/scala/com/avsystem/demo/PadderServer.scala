package com.avsystem.demo

import java.io.File
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.avsystem.commons.concurrent.HasRunInQueueEC
import com.avsystem.commons.concurrent.RunInQueueEC.Implicits.executionContext
import com.avsystem.demo.UPickleRPC._
import org.eclipse.jetty.http.{HttpMethod, HttpStatus}
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Request, Server}
import upickle.Js

import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success}

object PadderServer {
  def main(args: Array[String]): Unit = {
    val server = new Server(8080)
    server.setHandler(new Handler(handleAjaxRequest))
    server.start()
  }

  def handleAjaxRequest(input: String): Future[String] = {
    val RawInvocation(rpcName, argLists) = jsToRawInvocation(upickle.json.read(input))

    val realPadder: Padder = PadderImpl
    val rawPadder: RawRPC = AsRawRPC[Padder].asRaw(realPadder)
    rawPadder.call(rpcName, argLists)
      .map(value => Js.Obj("success" -> value))
      .recover({ case cause => Js.Obj("failure" -> Js.Str(cause.getMessage)) })
      .map(response => upickle.json.write(response))
  }

  private def jsToRawInvocation(js: Js.Value): RawInvocation = js match {
    case obj: Js.Obj =>
      val map = obj.value.toMap
      val rpcName = map("rpcName") match {
        case Js.Str(str) => str
        case _ => fail("expected JS string")
      }
      val argLists = map("argLists") match {
        case arr: Js.Arr => arr.value.toList.map {
          case innerArr: Js.Arr => innerArr.value.toList
          case _ => fail("expected JS array")
        }
        case _ => fail("expected JS array")
      }
      RawInvocation(rpcName, argLists)

    case _ => fail("expected JS object")
  }

  private def fail(msg: String) = throw new Exception(msg)
}

final class Handler(handleAjax: String => Future[String]) extends AbstractHandler with HasRunInQueueEC {

  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) = {
    baseRequest.setHandled(true)

    HttpMethod.fromString(request.getMethod) match {
      case HttpMethod.GET =>
        val file = new File(request.getRequestURI.stripPrefix("/"))
        if (file.isFile) {
          response.getWriter.write(Source.fromFile(file).getLines().mkString("\n"))
        } else {
          response.sendError(HttpStatus.NOT_FOUND_404)
        }

      case HttpMethod.POST =>
        val async = request.startAsync()

        val reader = request.getReader
        val input = Iterator.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")

        handleAjax(input).onComplete {
          case Success(value) =>
            response.getWriter.write(value)
            async.complete()
          case Failure(cause) =>
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500)
            async.complete()
        }

      case _ =>
        response.sendError(HttpStatus.BAD_REQUEST_400)
    }
  }
}
