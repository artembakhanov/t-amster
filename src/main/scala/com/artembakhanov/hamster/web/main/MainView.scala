package com.artembakhanov.hamster.web.main

import com.artembakhanov.hamster.web.korolev.View
import korolev.state.javaSerialization.*
import korolev.Context
import zio.ZIO
import levsha.events.EventPhase
import com.artembakhanov.hamster.web.korolev.AppState
import com.artembakhanov.hamster.web.korolev.ViewContext
import zio.ZLayer
import monocle.syntax.all.*

class MainView(context: ViewContext[AppState, MainState]) extends View[AppState, MainState](context):
  import levsha.dsl.*
  import levsha.dsl.html.*

  private def tap(state: MainState): MainState =
    if state.userInfo.gameInfo.value.remaining >= 10 then
      state
        .focus(_.userInfo.gameInfo.value.count)
        .modify(_ + 10)
        .focus(_.userInfo.gameInfo.value.remaining)
        .modify(_ - 10)
    else state

  private inline def level(count: Long): String =
    if count < 1000 then "0"
    else if count < 2000 then "1"
    else if count < 3500 then "2"
    else if count < 5000 then "3"
    else if count < 10000 then "4"
    else if count < 15000 then "5"
    else if count < 25000 then "6"
    else if count < 50000 then "7"
    else if count < 100_000 then "8"
    else if count < 250_000 then "9"
    else "10"

  def view(state: MainState): Node =
    val gameInfo = state.userInfo.gameInfo.value
    optimize:
      div(
        // clazz := "bg-gray-100 min-h-[calc(100dvh)] flex flex-col justify-between",
        clazz := "bg-gray-100 flex flex-col justify-between",
        height @= "var(--tg-viewport-stable-height)",
        header(
          clazz := "text-center py-4 bg-gray-800 text-white shadow-2xl z-10",
          h1(clazz := "text-3xl font-bold", state.userInfo.name),
          div(
            clazz := "flex justify-center space-x-4 mt-2",
            div(
              clazz := "bg-amber-200 px-4 py-2 rounded-lg shadow-md",
              p(clazz := "text-lg font-semibold text-gray-800", "Credits"),
              p(clazz := "text-xl text-gray-800", gameInfo.count.toString),
            ),
            div(
              clazz := "bg-blue-200 px-4 py-2 rounded-lg shadow-md",
              p(clazz := "text-lg font-semibold text-gray-800", "Profit per Sec"),
              p(clazz := "text-xl text-gray-800", "120"),
            ),
            div(
              clazz := "bg-green-200 px-4 py-2 rounded-lg shadow-md",
              p(clazz := "text-lg font-semibold text-gray-800", "Level"),
              p(clazz := "text-xl text-gray-800", level(gameInfo.allTimeMax)),
            ),
          ),
        ),
        state.tab match
          case Tab.Tap =>
            main(
              clazz := "flex-grow flex flex-col items-center justify-center bg-gradient-to-r from-purple-300 to-pink-400 shadow-2xl relative z-20",
              TagDef("touch-wrap")(
                button(
                  clazz := "w-56 h-56 bg-white rounded-full shadow-lg flex items-center justify-center mb-4 mt-8",
                  img(
                    src   := "/static/chomik.png",
                    alt   := "Hamster Image",
                    clazz := "w-52 h-52 rounded-full bg-zinc-800",
                  ),
                ),
                event("t0")(_.transition(tap)),
                event("t1")(_.transition(tap)),
                event("t2")(_.transition(tap)),
                event("t3")(_.transition(tap)),
                event("t4")(_.transition(tap)),
                event("t5")(_.transition(tap)),
                event("t6")(_.transition(tap)),
                event("t7")(_.transition(tap)),
                event("t8")(_.transition(tap)),
                event("t9")(_.transition(tap)),
              ),
              p(clazz := "text-2xl font-semibold text-white mb-8", s"${gameInfo.remaining}/3000"),
            )
          case Tab.Mine =>
            main(
              clazz := "flex-grow flex flex-col bg-gray-800 py-4 shadow-md text-white",
              div(
                clazz := "grid grid-cols-2 gap-4 px-4",
                button(
                  clazz := "flex flex-col items-center bg-gray-700 rounded-lg p-4 shadow-lg",
                  span(clazz := "text-lg font-semibold", "⛏️"),
                  p(clazz    := "text-sm text-gray-300 font-semibold", "Increases mining speed"),
                  p(clazz    := "text-sm text-gray-300", "5/sec (Lvl. 0)"),
                ),
                button(
                  clazz := "flex flex-col items-center bg-gray-700 rounded-lg p-4 shadow-lg",
                  span(clazz := "text-lg font-semibold", "🤲"),
                  p(clazz    := "text-sm text-gray-300 font-semibold", "Enhances resource collection"),
                  p(clazz    := "text-sm text-gray-300", "7/sec (Lvl. 0)"),
                ),
                button(
                  clazz := "flex flex-col items-center bg-gray-700 rounded-lg p-4 shadow-lg",
                  span(clazz := "text-lg font-semibold", "😮‍💨"),
                  p(clazz    := "text-sm text-gray-300 font-semibold", "Reduces mining fatigue"),
                  p(clazz    := "text-sm text-gray-300", "4/sec (Lvl. 0)"),
                ),
                button(
                  clazz := "flex flex-col items-center bg-gray-700 rounded-lg p-4 shadow-lg",
                  span(clazz := "text-lg font-semibold", "🦾"),
                  p(clazz    := "text-sm text-gray-300 font-semibold", "Improves tool durability"),
                  p(clazz    := "text-sm text-gray-300", "6/sec (Lvl. 0)"),
                ),
              ),
            ),
        footer(
          clazz := "bg-gray-800 py-4 shadow-md",
          div(
            clazz := "flex justify-around text-white",
            button(
              clazz := "flex flex-col items-center",
              span(clazz := s"text-lg font-semibold${if state.tab == Tab.Tap then "" else "text-gray-400"}", "Click"),
              eventZIO("click")(ZIO.serviceWithZIO[Access](_.transition(_.copy(tab = Tab.Tap)))),
            ),
            button(
              clazz := "flex flex-col items-center",
              span(clazz := s"text-lg font-semibold${if state.tab == Tab.Mine then "" else "text-gray-400"}", "Mine"),
              eventZIO("click")(ZIO.serviceWithZIO[Access](_.transition(_.copy(tab = Tab.Mine)))),
            ),
          ),
        ),
      )
  end view
end MainView

object MainView:
  val layer = ZLayer.derive[MainView]
