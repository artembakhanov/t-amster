package com.artembakhanov.hamster.web.korolev

import zio.*
import korolev.Extension
import com.artembakhanov.hamster.domain.DataService
import zio.json.JsonDecoder
import zio.json.*
import com.artembakhanov.hamster.domain.UserInfo
import com.artembakhanov.hamster.web.main.MainState
import com.artembakhanov.hamster.domain.GameInfo
import zio.cache.Cache
import zio.cache.Lookup
import java.time.temporal.ChronoUnit
import monocle.syntax.all.*
import korolev.Context.BaseAccess

object Extensions:
  private case class LoginInfo(id: Long, firstName: String, lastName: String) derives JsonDecoder

  private def loginCallback(dataService: DataService, stateCache: Cache[Long, Nothing, Ref[UserInfo]])(using
      Runtime[Any],
  ) = Extension[Task, AppState, Any](access =>
    Promise.make[Nothing, Ref[UserInfo]].flatMap { promise =>
      access
        .registerCallback("login") { s =>
          for
            loginInfo <- ZIO
              .fromEither(s.fromJson[LoginInfo])
              .mapError(error => Exception(s"Error while parsing login info: $error"))
            newUserInfo = UserInfo(id = loginInfo.id, name = s"${loginInfo.firstName} ${loginInfo.lastName}")
            userInfo    <- dataService.getOrCreateUser(newUserInfo)
            gameInfoRef <- stateCache.get(userInfo.id)
            _           <- promise.succeed(gameInfoRef)
            state       <- gameInfoRef.get
            _           <- access.transition(_ => AppState.Authorized(AuthorizationLevel.User, MainState(state)))
          yield ()
        }
        .flatMap(_ =>
          for
            ref   <- promise.await
            scope <- Scope.make
            _ <-
              ref.get
                .flatMap(userInfo => dataService.updateGameInfo(userInfo.id, userInfo.gameInfo.value))
                .logError("There was an error while saving progress")
                .ignore
                .repeat(Schedule.fixed(10.seconds))
                .forkIn(scope)

            _ <- updateCounters(access).ignore.repeat(Schedule.fixed(1.second)).forkIn(scope)
          yield (ref, scope),
        )
        .map((ref, scope) =>
          Extension.Handlers(
            onState = {
              case state: AppState.Authorized =>
                ref.set(state.state.userInfo)
              case _ => ZIO.unit
            },
            onDestroy = () =>
              ref.get
                .flatMap(userInfo =>
                  dataService
                    .updateGameInfo(userInfo.id, userInfo.gameInfo.value),
                )
                .logError("There was an error while saving progress (final)")
                .ignore *> scope.close(Exit.unit),
          ),
        )
    },
  )

  private def updateCounters(access: BaseAccess[Task, AppState, Any]): ZIO[Any, Throwable, Unit] =
    for
      now_ <- Clock.instant
      now = now_.truncatedTo(ChronoUnit.SECONDS)

      _ <- access.maybeTransition { case authorized: AppState.Authorized =>
        val increaseRemaining =
          java.time.Duration
            .between(authorized.state.userInfo.gameInfo.value.lastUpdatedRemaining, now)
            .toSeconds() * 10

        authorized
          .focus(_.state.userInfo.gameInfo.value)
          .modify(
            _.focus(_.lastUpdatedRemaining)
              .set(now)
              .focus(_.remaining)
              .modify(previous => (previous + increaseRemaining).min(3000)),
          )
      }
    yield ()

  val layer = ZLayer {
    for
      given Runtime[Any] <- ZIO.runtime
      dataService        <- ZIO.service[DataService]
      stateCache <- Cache.make[Long, Any, Nothing, Ref[UserInfo]](
        2048,
        1.hour,
        Lookup(id =>
          for
            info <- dataService.getUser(id).orElseSucceed(UserInfo(id, "Errored user"))
            ref  <- Ref.make[UserInfo](info)
          yield ref,
        ),
      )
    yield List(loginCallback(dataService, stateCache))
  }
end Extensions
