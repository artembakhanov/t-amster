package com.artembakhanov.hamster

import zio.{ExitCode, RIO, Runtime, ZIO, ZIOAppDefault}
import korolev.Context
import korolev.server.{KorolevServiceConfig, StateLoader}
import korolev.web.PathAndQuery
import korolev.zio.Zio2Effect
import zio.http.HttpApp
import korolev.state.javaSerialization.*
import korolev.zio.http.ZioHttpKorolev
import zio.http.Response
import zio.http.Server
import zio.http.Status
import scala.concurrent.ExecutionContext
import zio.http.Routes
import com.artembakhanov.hamster.web.WebRoutes
import zio.http.SSLConfig
import zio.http.Middleware

object Main extends ZIOAppDefault:
  val sslConfig = SSLConfig.fromFile(
    behaviour = SSLConfig.HttpBehaviour.Accept,
    certPath = "./certificate.crt",
    keyPath = "./private.key",
  )
  override def run =
    for
      webRoutes <- WebRoutes.make
      _ <- Server
        .serve(webRoutes.@@(Middleware.debug).handleError(_ => Response.badRequest))
        .provide(Server.defaultWith(_.port(8089).ssl(sslConfig)))
        .orDie
    yield ExitCode.success
end Main
