package com.example.client

import com.example.client.App.serviceStub
import com.example.service.Request
import com.example.service.Response
import io.grpc.stub.StreamObserver
import scala.scalajs.js.timers.{clearTimeout, setTimeout}
import scala.util.chaining.scalaUtilChainingOps
import scalapb.grpcweb.Metadata
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks.{useEffect, useState}
import slinky.web.html.{div, h2, p}

@react
object Stream {
  case class Props(cancel: Boolean)

  val component = FunctionalComponent[Props] { props =>
    useState(("Request pending", 0)).pipe { case ((status, _), setStatus) =>
      useEffect(
        () => {
          serviceStub
            .serverStreaming(
              Request(payload = "Hello!"),
              Metadata("custom-header-2" -> "streaming-value"),
              new StreamObserver[Response] {
                override def onNext(value: Response): Unit =
                  setStatus({ case (_, count) => (s"Received success [$count]", count + 1) })

                override def onError(ex: Throwable): Unit =
                  setStatus({ case (_, count) => (s"Received failure: $ex", count) })

                override def onCompleted(): Unit =
                  setStatus({ case (_, count) => (s"Received completed", count) })
              }
            )
            .pipe(stream =>
              setStatus(("Request sent", resCount))
                .pipe(_ =>
                  if (props.cancel)
                    Some(setTimeout(5000) {
                      setStatus({ case (_, count) => (s"Stream stopped by client", count) })
                      stream.cancel()
                    })
                  else
                    None
                )
                .pipe(maybeTimer =>
                  () => stream.cancel().tap(_ => maybeTimer.foreach(clearTimeout))
                )
            )
        },
        Seq.empty
      ).pipe(_ =>
        div(
          h2("Stream request:"),
          p(status)
        )
      )
    }
  }
}
