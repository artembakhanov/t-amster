package com.artembakhanov.hamster.web.korolev

import zio.*
import korolev.server.StateLoader
import korolev.web.PathAndQuery
import korolev.server.KorolevServiceConfig
import korolev.zio.Zio2Effect
import levsha.Document
import korolev.Context
import com.artembakhanov.hamster.web.main.MainState
import korolev.Extension
import scala.concurrent.duration.*
import levsha.dsl.html

object Config:
  val layer = ZLayer:
    for
      given Runtime[Any] <- ZIO.runtime[Any]
      appView            <- ZIO.service[AppView]
      extensions         <- ZIO.service[List[Extension[Task, AppState, Any]]]
    yield KorolevServiceConfig[Task, AppState, Any](
      stateLoader =
        /* StateLoader.default(AppState.Authorized(AuthorizationLevel.User, MainState(0))), // todo: fix for
         * authorization */
        StateLoader.default(AppState.NotAuthorized), // todo: fix for authorization
      rootPath = PathAndQuery.Root,
      presetIds = true,
      document = appView.view,
      extensions = extensions,
      heartbeatInterval = 2.seconds,
      sessionIdleTimeout = 5.seconds,
      connectionLostWidget = html.div(html.clazz := "blur-overlay", html.div(html.clazz := "z-40 loader")),
    )
end Config
