package com.artembakhanov.hamster.domain

import zio.ZIO
import java.sql.SQLException

trait DataService:
  def getOrCreateUser(user: UserInfo): ZIO[Any, SQLException, UserInfo]

  def getUser(userId: Long): ZIO[Any, SQLException, UserInfo]

  def updateGameInfo(userId: Long, gameInfo: GameInfo): ZIO[Any, SQLException, Unit]
end DataService
