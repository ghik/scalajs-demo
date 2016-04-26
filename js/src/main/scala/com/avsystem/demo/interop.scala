package com.avsystem

import scala.scalajs.js

package demo {

  import scala.scalajs.js.annotation.JSExport

  @js.native
  object Globals extends js.GlobalScope {
    def createApple(`type`: String): Apple = js.native
  }

  @js.native
  trait Apple extends js.Object {
    val `type`: String = js.native
    var color: String = js.native
    def getInfo: String = js.native
  }

  object test {

    @JSExport
    val func: js.Function1[Apple,Unit] = (apple: Apple) => {
      apple.color = "green"
    }

    @JSExport
    val thisFunc: js.ThisFunction0[Apple,Unit] = (apple: Apple) => {
      apple.color = "green"
    }

  }

}



