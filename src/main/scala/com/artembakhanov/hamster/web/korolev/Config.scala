package com.artembakhanov.hamster.web.korolev

import zio.*
import korolev.server.StateLoader
import korolev.web.PathAndQuery
import korolev.server.KorolevServiceConfig
import korolev.zio.Zio2Effect
import levsha.Document
import korolev.Context
import com.artembakhanov.hamster.web.main.MainState

object Config:
  val layer = ZLayer:
    for
      given Runtime[Any] <- ZIO.runtime[Any]
      appView           <- ZIO.service[AppView]
    yield KorolevServiceConfig[Task, AppState, Any](
      stateLoader =
        StateLoader.default(AppState.Authorized(AuthorizationLevel.User, MainState(0))), // todo: fix for authorization
      rootPath = PathAndQuery.Root,
      presetIds = true,
      document = appView.view,
    )
