val scala3Version = "3.4.2"
val korolevVersion = "1.7.0-M1.4"
val korolevZioHttpVersion = "1.7.0-M1.8-SNAPSHOT"
val zioVersion = "2.1.5"

ThisBuild / scalacOptions += "-Ykind-projector:underscores"

lazy val root = project
  .in(file("."))
  .settings(
    name := "hamster",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.fomkin" %% "korolev-zio-http" % korolevZioHttpVersion,
      "org.fomkin" %% "korolev-zio2" % korolevZioHttpVersion,
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-streams" % zioVersion
    )
  )
