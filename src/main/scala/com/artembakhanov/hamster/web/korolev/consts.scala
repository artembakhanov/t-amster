package com.artembakhanov.hamster.web.korolev

import scala.concurrent.ExecutionContext
import zio.*
import korolev.zio.Zio2Effect
import korolev.Context
import korolev.util.Lens
import com.artembakhanov.hamster.web.main.MainState

// execution context is not used but mandatory
given ExecutionContext = Runtime.defaultExecutor.asExecutionContext
given (using runtime: Runtime[Any]): Zio2Effect[Any, Throwable] =
  new Zio2Effect[Any, Throwable](runtime, identity, identity)

type ViewContext[PS, S] = Context.Scope[ZIO[Any, Throwable, _], PS, S, Any]

object Scopes:
  val appScope = Context[ZIO[Any, Throwable, _], AppState, Any]
  val mainLens = Lens[AppState, MainState](
    { case AppState.Authorized(level, state) => state },
    { case (a: AppState.Authorized, mainState) => a.copy(state = mainState) },
  )
  val mainScope = appScope.scope(mainLens)
  val layer     = ZLayer.succeed(mainScope) ++ ZLayer.succeed(appScope)
end Scopes
