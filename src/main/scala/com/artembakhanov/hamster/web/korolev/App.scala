package com.artembakhanov.hamster.web.korolev

import zio.ZLayer
import zio.ZIO
import com.artembakhanov.hamster.web.main.MainView
import korolev.Context

class AppView(mainView: MainView) extends View[AppState, AppState](Context[ZIO[Any, Throwable, _], AppState, Any]):
  import levsha.dsl.*
  import levsha.dsl.html.*

  def view(state: AppState): Node = optimize:
    Html(
      head(
        script(src := "https://cdn.tailwindcss.com"),
        script(src := "https://telegram.org/js/telegram-web-app.js"),
        script(src := "/static/CustomElements.js"),
        link(rel   := "stylesheet", href := "/static/main.css"),
        meta(
          name    := "viewport",
          content := "width=device-width, initial-scale=1.0, maximum-scale=2.0, user-scalable=no",
        ),
      ),
      body(state match
        case AppState.NotAuthorized =>
          div(clazz := "loader")

        case AppState.Authorized(level, state) =>
          mainView.view(state),
      ),
    )
end AppView

object AppView:
  val layer = ZLayer.derive[AppView]
