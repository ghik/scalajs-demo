package com.avsystem

package demo {

  import scala.scalajs.js.annotation.{JSExport, JSExportAll}

  @JSExport
  @JSExportAll
  object Utils {
    def echo(str: String): Unit = println(str)
  }

  @JSExport
  @JSExportAll
  class Cat(val weight: Int) {
    def meow(): Unit = println("meow")
  }

}
