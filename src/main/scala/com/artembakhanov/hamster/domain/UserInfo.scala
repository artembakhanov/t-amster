package com.artembakhanov.hamster.domain

import io.getquill.JsonValue

final case class UserInfo(id: Long, name: String, gameInfo: JsonValue[GameInfo] = JsonValue(GameInfo.empty))
