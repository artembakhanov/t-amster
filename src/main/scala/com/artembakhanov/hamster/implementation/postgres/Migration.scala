package com.artembakhanov.hamster.implementation.postgres

import zio.*
import org.flywaydb.core.Flyway

class Migration(dbConfig: DbConfig):
  def perform: Task[Unit] =
    ZIO
      .attemptBlocking:
        Flyway.configure
          .dataSource(dbConfig.jdbcUri, dbConfig.user, dbConfig.password)
          .schemas("public")
          .baselineOnMigrate(true)
          .load
          .migrate
      .tapError(error => ZIO.logError(s"Migration Error: ${error.getMessage}"))
      .unit
end Migration

object Migration:
  val layer = ZLayer.derive[Migration]
