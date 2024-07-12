package com.artembakhanov.hamster.implementation.postgres

final case class DbConfig(host: String, port: Int, user: String, password: String, database: String):
  val jdbcUri =
    val location    = s"${host}:${port}/${database}"
    val credentials = s"user=$user&password=$password"

    s"jdbc:postgresql://$location?$credentials"
  end jdbcUri
end DbConfig
