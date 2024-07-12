package com.artembakhanov.hamster.implementation.postgres

import javax.sql.DataSource
import com.zaxxer.hikari.HikariConfig
import zio.*
import com.zaxxer.hikari.HikariDataSource

object PostgresDataSource:
  val layer: RLayer[DbConfig, DataSource] =
    ZLayer(ZIO.serviceWith[DbConfig] { db =>
      val hikariConfig = new HikariConfig()
      hikariConfig.setDriverClassName("org.postgresql.Driver")
      hikariConfig.setJdbcUrl(db.jdbcUri)
      hikariConfig.setUsername(db.user)
      hikariConfig.setPassword(db.password)

      new HikariDataSource(hikariConfig)
    })
end PostgresDataSource
