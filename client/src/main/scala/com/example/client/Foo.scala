package com.example.client

import org.scalajs.dom.ext.Ajax.get
import scala.util.chaining.scalaUtilChainingOps
import slinky.core.{FunctionalComponent, StatelessComponent}
import slinky.core.annotations.react
import slinky.core.facade.Hooks.{useEffect, useState}
import slinky.core.facade.Fragment
import slinky.web.html.h1
import zio.{Runtime, ZIO}

@react
object Foo {
  type Props = Unit
  val component: FunctionalComponent[Props] = FunctionalComponent { _ =>
    useState("").pipe { case (text, setText) =>
      useEffect(() =>
        Runtime.default.unsafeRunAsync_(ZIO.fromFuture(context => get("https://swapi.dev/api/people/").map(_.responseText)(context)).fold(
          _ => setText("Ajax failed"),
          setText(_)
        ))
      ).pipe(_ =>
        Fragment(
          h1("Goodbye world!"),
          h1(text)
        )
      )
    }
  }
}