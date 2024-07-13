package com.artembakhanov.hamster.domain

import io.getquill.JsonValue
import zio.json.JsonEncoder
import zio.json.DeriveJsonEncoder

final case class UserInfo(id: Long, name: String, gameInfo: JsonValue[GameInfo] = JsonValue(GameInfo.empty))
    derives JsonEncoder

object UserInfo:
  given [A](using JsonEncoder[A]): JsonEncoder[JsonValue[A]] = DeriveJsonEncoder.gen[JsonValue[A]]
