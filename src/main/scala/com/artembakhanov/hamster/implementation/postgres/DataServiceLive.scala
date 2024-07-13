package com.artembakhanov.hamster.implementation.postgres

import javax.sql.DataSource
import com.artembakhanov.hamster.domain.DataService
import java.sql.SQLException
import com.artembakhanov.hamster.domain.UserInfo
import zio.*
import io.getquill.context.qzio.ImplicitSyntax.Implicit
import com.artembakhanov.hamster.domain.GameInfo
import zio.json.JsonEncoder
import zio.json.DeriveJsonEncoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder

class DataServiceLive(dataSource: DataSource) extends DataService:
  import io.getquill.*
  import io.getquill.context.json.PostgresJsonExtensions
  import io.getquill.context.ZioJdbc.*
  import com.artembakhanov.hamster.implementation.postgres.DbContext
  import com.artembakhanov.hamster.implementation.postgres.DbContext.*

  given implicitDS: Implicit[DataSource] = Implicit(dataSource)

  override def getOrCreateUser(user: UserInfo): ZIO[Any, SQLException, UserInfo] =
    DbContext
      .run(query[UserInfo].insertValue(lift(user)).onConflictUpdate(_.id)((t, e) => t.name -> e.name).returning(u => u))
      .implicitDS

  override def updateGameInfo(userId: Long, gameInfo: GameInfo): ZIO[Any, SQLException, Unit] =
    DbContext
      .run(query[UserInfo].filter(_.id == lift(userId)).update(_.gameInfo -> lift(JsonValue(gameInfo))))
      .implicitDS
      .unit

  override def getUser(userId: Long): ZIO[Any, SQLException, UserInfo] =
    DbContext
      .run(query[UserInfo].filter(_.id == lift(userId)).value)
      .implicitDS
      .someOrFail(SQLException("Does not exist"))

  override def getAllUsers: ZIO[Any, SQLException, List[UserInfo]] =
    DbContext
      .run(query[UserInfo])
      .implicitDS

end DataServiceLive

object DataServiceLive:
  val layer = ZLayer.derive[DataServiceLive]
