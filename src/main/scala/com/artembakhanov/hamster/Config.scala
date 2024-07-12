package com.artembakhanov.hamster

import com.artembakhanov.hamster.implementation.postgres.DbConfig
import zio.ConfigProvider
import zio.*
import zio.config.magnolia.deriveConfig
import zio.config.*

type ConfigEnv = DbConfig

final case class Config(db: DbConfig)

object Config:
  val layer: TaskLayer[ConfigEnv] =
    val configLayer = ZLayer(envSource.orElse(fallbackSource).load(deriveConfig[Config].toKebabCase.toUpperCase))

    configLayer.project(_.db)
  end layer

  final private val envSource = ConfigProvider.fromEnv(pathDelim = "_", seqDelim = ",")

  final private val fallbackSource = ConfigProvider.fromMap(
    Map(
      "DB_HOST"     -> "localhost",
      "DB_PORT"     -> "5432",
      "DB_USER"     -> "user",
      "DB_PASSWORD" -> "password",
      "DB_DATABASE" -> "hamster",
    ),
    pathDelim = "_",
    seqDelim = ",",
  )
end Config
