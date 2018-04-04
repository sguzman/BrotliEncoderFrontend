package com.github.sguzman.brotli.frontend

import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.html.Div

object Main {
  @dom def render: Binding[Div] = {
    <div>
      <h1>Hello</h1>
    </div>
  }

  def main(args: Array[String]): Unit = {
    println("Hello")
  }
}
