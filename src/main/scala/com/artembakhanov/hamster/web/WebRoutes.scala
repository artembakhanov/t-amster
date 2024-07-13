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
import zio.http.*
import com.artembakhanov.hamster.domain.DataService
import zio.json.*
import zio.json.given
import com.artembakhanov.hamster.domain.UserInfo

object WebRoutes:
  private case class LeaderboardEntry(user: UserInfo, score: Long) derives JsonEncoder
  private val leaderboardRoute: ZIO[DataService, Nothing, Route[Any, Nothing]] =
    ZIO.service[DataService].map { dataService =>
      Method.GET / "leaderboard" -> handler { (req: Request) =>
        for
          users <- dataService.getAllUsers.orElseSucceed(List.empty)
          usersWithScores = users
            .sortBy(u => -u.gameInfo.value.perSec)
            .map(userInfo => LeaderboardEntry(userInfo, (userInfo.gameInfo.value.perSec * 60).toLong))
        yield Response.json(usersWithScores.toJsonPretty)
      }
    }

  private val adminRoutesZIO = leaderboardRoute.map(Routes(_))

  val make =
    val effect =
      for
        given Runtime[Any] <- ZIO.runtime[Any]
        appConfig          <- ZIO.service[KorolevServiceConfig[Task, AppState, Any]]
        _                  <- ZIO.serviceWithZIO[Migration](_.perform)
        adminRoutes        <- adminRoutesZIO
      yield ZioHttpKorolev[Any].service(appConfig) ++ adminRoutes

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
