package com.artembakhanov.hamster.web

import zio.*
import com.artembakhanov.hamster.web.main.MainView
import com.artembakhanov.hamster.web.korolev.AppView
import _root_.korolev.server.KorolevServiceConfig
import com.artembakhanov.hamster.web.korolev.AppState
import com.artembakhanov.hamster.web.korolev.Config
import com.artembakhanov.hamster.web.korolev.Scopes
import com.artembakhanov.hamster.web.korolev.Extensions
import _root_.korolev.zio.http.ZioHttpKorolev
import _root_.korolev.state.javaSerialization.*
import com.artembakhanov.hamster.web.korolev.given
import com.artembakhanov.hamster.implementation.postgres.DataServiceLive
import com.artembakhanov.hamster.implementation.postgres.PostgresDataSource
import com.artembakhanov.hamster.implementation.postgres.Migration

object WebRoutes:
  val make =
    val effect =
      for
        given Runtime[Any] <- ZIO.runtime[Any]
        appConfig          <- ZIO.service[KorolevServiceConfig[Task, AppState, Any]]
        _                  <- ZIO.serviceWithZIO[Migration](_.perform)
      yield ZioHttpKorolev[Any].service(appConfig)

    effect.provide(
      Config.layer,
      AppView.layer,
      MainView.layer,
      Scopes.layer,
      Extensions.layer,
      DataServiceLive.layer,
      PostgresDataSource.layer,
      com.artembakhanov.hamster.Config.layer,
      Migration.layer,
    )
  end make

end WebRoutes
