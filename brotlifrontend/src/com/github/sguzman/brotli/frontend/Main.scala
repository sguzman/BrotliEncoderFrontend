package com.github.sguzman.brotli.frontend

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.{Event, document}
import org.scalajs.dom.html.{Div, Input}
import org.scalajs.dom.raw.Element

import scala.language.reflectiveCalls

object Main {
  implicit final class StrWrap(str: String) {
    def id[A] = document.getElementById(str).asInstanceOf[A]
  }

  final case class Model(user: Var[String], repo: Var[String])
  val model = Model(Var(""), Var(""))

  sealed abstract class UpdateType(e: Event) {
    def `type` = e.`type`
  }

  final case class onUserInput(e: Event) extends UpdateType(e)
  final case class onRepoInput(e: Event) extends UpdateType(e)
  final case class onNil(e: Event) extends UpdateType(e)

  def update(msg: UpdateType): Unit = {
    msg match {
      case _: onUserInput =>
        model.user.value = "user".id[Input].value
      case _: onRepoInput =>
        model.repo.value = "repo".id[Input].value
      case _: onNil => {}
    }
  }

  def emit[A <: Element](e: Event): Unit = {
    val target = e.currentTarget.asInstanceOf[A].id
    val typ = e.`type`

    val msg = target match {
      case "repo" => typ match {
        case "input" => onRepoInput(e)
      }
      case "user" => typ match {
        case "input" => onUserInput(e)
      }
      case _ => onNil(e)
    }

    update(msg)
  }

  @dom def view(model: Model): Binding[Div] = {
    <div>
      <h1>Hello</h1>
      <input id="user" oninput={emit[Input] _}></input>
      <input id="repo" oninput={emit[Input] _}></input>
      <p>User: {model.user.bind}</p>
      <p>Repo: {model.repo.bind}</p>
    </div>
  }

  def main(args: Array[String]): Unit = {
    com.thoughtworks.binding.dom.render(org.scalajs.dom.document, view(model))
  }
}
