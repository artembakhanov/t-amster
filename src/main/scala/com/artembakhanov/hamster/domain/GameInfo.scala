package com.artembakhanov.hamster.domain

import zio.json.JsonDecoder
import zio.json.JsonEncoder
import java.time.Instant

final case class GameInfo(count: Long, remaining: Long, lastUpdatedRemaining: Instant) derives JsonDecoder, JsonEncoder

object GameInfo:
  val empty = GameInfo(count = 0, remaining = 3000, lastUpdatedRemaining = Instant.MIN)
