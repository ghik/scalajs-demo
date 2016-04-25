package com.avsystem.demo

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.avsystem.commons.concurrent.HasRunInQueueEC
import com.avsystem.demo.UPickleRPC._
import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Request, Server}
import upickle.Js

import scala.io.Source
import scala.util.{Failure, Success}

/**
  * Author: ghik
  * Created: 25/04/16.
  */
object Backend {
  def main(args: Array[String]): Unit = {
    val server = new Server(8080)
    server.setHandler(LeftPadHandler)
    server.start()
  }
}

object LeftPadHandler extends AbstractHandler with HasRunInQueueEC {
  private def fail(msg: String) = throw new Exception(msg)

  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) = {
    baseRequest.setHandled(true)

    HttpMethod.fromString(request.getMethod) match {
      case HttpMethod.GET =>
        response.getWriter.write(Source.fromFile(request.getRequestURI.stripPrefix("/")).getLines().mkString("\n"))

      case HttpMethod.POST =>
        val async = request.startAsync()

        val reader = request.getReader
        val content = Stream.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
        val RawInvocation(rpcName, argLists) = jsToRawInvocation(upickle.json.read(content))

        val rawLeftPadder = UPickleRPC.AsRawRPC[Padder].asRaw(PadderImpl)
        rawLeftPadder.call(rpcName, argLists).onComplete {
          case Success(value) => sendResponse(Js.Obj("success" -> value))
          case Failure(cause) => sendResponse(Js.Obj("failure" -> Js.Str(cause.getMessage)))
        }

        def sendResponse(rawResponse: Js.Value): Unit = {
          response.getWriter.write(upickle.json.write(rawResponse))
          async.complete()
        }

      case _ => fail("only POST allowed")
    }
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
}
