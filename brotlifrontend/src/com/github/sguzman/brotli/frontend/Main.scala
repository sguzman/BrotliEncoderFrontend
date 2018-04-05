package com.github.sguzman.brotli.frontend

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.html.{Html, Input}
import org.scalajs.dom.raw.Element
import org.scalajs.dom.{Event, document}

import scala.language.reflectiveCalls

object Main {
  implicit final class StrWrap(str: String) {
    def id[A] = document.getElementById(str).asInstanceOf[A]
  }

  final case class Model(user: Var[String], repo: Var[String], branch: Var[String], file: Var[String], brotli: Var[Boolean])
  val model = Model(Var(""), Var(""), Var(""), Var(""), Var(false))

  sealed abstract class UpdateType(e: Event) {
    def `type` = e.`type`
  }

  final case class onUserInput(e: Event) extends UpdateType(e)
  final case class onRepoInput(e: Event) extends UpdateType(e)
  final case class onBranchInput(e: Event) extends UpdateType(e)
  final case class onFileInput(e: Event) extends UpdateType(e)
  final case class onCheckBox(e: Event) extends UpdateType(e)

  def update(msg: UpdateType): Unit = {
    msg match {
      case _: onUserInput =>
        model.user.value = "user".id[Input].value
      case _: onRepoInput =>
        model.repo.value = "repo".id[Input].value
      case _: onBranchInput =>
        model.branch.value = "branch".id[Input].value
      case _: onFileInput =>
        model.file.value = "file".id[Input].value
      case _: onCheckBox =>
        model.brotli.value = !model.brotli.value
    }
  }

  def emit[A <: Element](e: Event): Unit = {
    val target = e.currentTarget.asInstanceOf[A].id
    val t = e.`type`

    val msg = (target, t) match {
      case ("repo", "input") => onRepoInput(e)
      case ("user", "input") => onUserInput(e)
      case ("branch", "input") => onBranchInput(e)
      case ("file", "input") => onFileInput(e)
      case ("brotli", "change") => onCheckBox(e)
      case _ => throw new Exception("Bad emit")
    }

    update(msg)
  }

  @dom def view(model: Model): Binding[Html] = {
    <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
      <title>Brotli Service</title>
    </head>
    <body>
      <div id="container">
        <header>
          <h1 id="header">Include your Web Dependencies with Optional Brotli Encoding</h1>
        </header>
        <div>
          <p>Add the user, repo, branch and file you plan on linking to</p>
          <p>If you plan on using Brotli encoding, don't forget to set the checkbox</p>
          <div id="inputs">
            <input id="user" oninput={emit[Input] _}></input>
            <input id="repo" oninput={emit[Input] _}></input>
            <input id="branch" oninput={emit[Input] _}></input>
            <input id="file" oninput={emit[Input] _}></input>
            <input type="checkbox" id="brotli" onchange={emit[Input] _}></input>
          </div>
        </div>
        <h3>Path:</h3>
        <p><b>https://brotli-encode.herokuapp.com/{model.user.bind}/{model.repo.bind}/{model.branch.bind}/{model.file.bind}{if (model.brotli.bind) "?brotli=true" else ""}</b></p>
        <p>Make sure the path above is valid and copy it anywhere a GitHub resources needs to be pulled</p>
        <div>
          Now you can use the generated path in your HTML
        </div>
      </div>
    </body>
    </html>
  }

  def main(args: Array[String]): Unit = {
    com.thoughtworks.binding.dom.render(org.scalajs.dom.document, view(model))
  }
}
