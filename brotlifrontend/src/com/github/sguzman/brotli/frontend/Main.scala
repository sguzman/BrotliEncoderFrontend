package com.github.sguzman.brotli.frontend

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.html.{Div, Input}
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
        println("checkbox".id[Input].value)
    }
  }

  def emit[A <: Element](e: Event): Unit = {
    val target = e.currentTarget.asInstanceOf[A].id
    val t = e.`type`

    val msg = target match {
      case "repo" => t match {
        case "input" => onRepoInput(e)
      }
      case "user" => t match {
        case "input" => onUserInput(e)
      }
      case "branch" => t match {
        case "input" => onBranchInput(e)
      }
      case "file" => t match {
        case "input" => onFileInput(e)
      }
      case "checkbox" => t match {
        case "input" => onCheckBox(e)
      }
    }

    update(msg)
  }

  @dom def view(model: Model): Binding[Div] = {
    <div>
      <h1>Add Brotli Compression encoding</h1>
      <h2>How it works</h2>
      <div>
        <p>Add the user, repo, branch and file you plan on linking to</p>
        <p>If you plan on using Brotli encoding, don't forget to set the checkmark</p>
        <input id="user" oninput={emit[Input] _}></input>
        <input id="repo" oninput={emit[Input] _}></input>
        <input id="branch" oninput={emit[Input] _}></input>
        <input id="file" oninput={emit[Input] _}></input>
        <input type="checkbox" id="brotli" onchange={emit[Input] _}></input>
      </div>
      <p>Path: https://brotli-encode.herokuapp.com/{model.user.bind}/{model.repo.bind}/{model.branch.bind}/{model.file.bind}{if (model.brotli.bind) "?brotli=true" else ""}</p>
      <p></p>
      <div>
        Now you can use the generated path in your HTML
      </div>
    </div>
  }

  def main(args: Array[String]): Unit = {
    com.thoughtworks.binding.dom.render(org.scalajs.dom.document, view(model))
  }
}
