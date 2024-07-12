val scala3Version         = "3.4.2"
val korolevVersion        = "1.7.0-M1.8-SNAPSHOT"
val korolevZioHttpVersion = "1.7.0-M1.8-SNAPSHOT"
val zioVersion            = "2.1.5"
val zioConfigVersion      = "4.0.2"
val quillJdbcVersion      = "4.8.4"
val postgresqlVersion     = "42.3.1"
val flywayVersion         = "9.16.0"
val zioJsonVersion        = "0.7.1"
val monocleVersion        = "3.1.0"
val zioCacheVersion       = "0.2.3"

ThisBuild / scalacOptions += "-Ykind-projector:underscores"

val assemblySettings = Seq(
  assembly / test               := {},
  assembly / assemblyJarName    := s"${name.value}-${version.value}.jar",
  assembly / assemblyOutputPath := baseDirectory.value / "target" / (assembly / assemblyJarName).value,
  assembly / assemblyMergeStrategy := {
    case PathList("io", "getquill", _*)       => MergeStrategy.first
    case PathList("META-INF", "services", _*) => MergeStrategy.first
    case PathList("META-INF", _*)             => MergeStrategy.discard
    case "application.conf"                   => MergeStrategy.concat
    case x if x.endsWith("module-info.class") => MergeStrategy.discard
    case x                                    => (assembly / assemblyMergeStrategy).value(x)
  },
)

lazy val root = project
  .in(file("."))
  .settings(
    name         := "hamster",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.fomkin"    %% "korolev-zio-http"    % korolevZioHttpVersion,
      "org.fomkin"    %% "korolev-zio2"        % korolevZioHttpVersion,
      "io.getquill"   %% "quill-jdbc-zio"      % quillJdbcVersion,
      "org.postgresql" % "postgresql"          % postgresqlVersion,
      "dev.zio"       %% "zio"                 % zioVersion,
      "dev.zio"       %% "zio-streams"         % zioVersion,
      "dev.zio"       %% "zio-concurrent"      % zioVersion,
      "dev.zio"       %% "zio-config"          % zioConfigVersion,
      "dev.zio"       %% "zio-config-magnolia" % zioConfigVersion,
      "dev.zio"       %% "zio-json"            % zioJsonVersion,
      "dev.zio"       %% "zio-cache"           % zioCacheVersion,
      "org.flywaydb"   % "flyway-core"         % flywayVersion,
      "dev.optics"    %% "monocle-core"        % monocleVersion,
      "dev.optics"    %% "monocle-macro"       % monocleVersion,
    ),
  )
  .settings(assemblySettings *)
