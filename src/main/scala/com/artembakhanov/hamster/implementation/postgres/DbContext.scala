package com.artembakhanov.hamster.implementation.postgres

import io.getquill.PostgresZioJdbcContext
import io.getquill.SnakeCase

given DbContext: PostgresZioJdbcContext[SnakeCase] = PostgresZioJdbcContext(SnakeCase)
