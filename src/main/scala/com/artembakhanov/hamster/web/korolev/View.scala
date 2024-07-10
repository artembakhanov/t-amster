package com.artembakhanov.hamster.web.korolev

import levsha.Document
import korolev.Context
import zio.*
import levsha.events.EventPhase

trait View[PS, S](context: ViewContext[PS, S]):
  export context.{event as _, ComponentDsl as _, JsCodeHelper as _, *}

  protected def event(name: String, stopPropagation: Boolean = false, phase: EventPhase = EventPhase.Bubbling) =
    context.event(name, stopPropagation, phase)

  protected def eventZIO(name: String, stopPropagation: Boolean = false, phase: EventPhase = EventPhase.Bubbling)(
      effect: ZIO[ViewContext[PS, S]#Access, Throwable, Unit],
  )(using Tag[S]): ViewContext[PS, S]#Event =
    event(name, stopPropagation, phase)(access => effect.provideLayer(ZLayer.succeed(access)))

  def view(state: S): ViewContext[PS, S]#Node
end View
