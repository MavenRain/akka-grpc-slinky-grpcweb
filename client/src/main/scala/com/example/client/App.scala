package com.example.client

import com.example.service.ServiceGrpcWeb
import io.grpc.ManagedChannel
import scala.scalajs.LinkingInfo
import scalapb.grpc.Channels
import scalapb.grpcweb.Metadata
import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, ReactElement}
import slinky.web.html.h1

@react
class App extends StatelessComponent {
  type Props = Unit

  def render(): ReactElement =
    Fragment(
      h1("Hello world!"),
      Unary(),
      Stream(cancel = false),
      Stream(cancel = true)
    )
}

object App {

  val serviceStub: ServiceGrpcWeb.Service[Metadata] =
    ServiceGrpcWeb.stub(
      Channels.grpcwebChannel(
        if (LinkingInfo.developmentMode)
          "http://localhost:9000"
        else
          ""
      )
    )
}
