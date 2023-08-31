import org.typelevel.scalacoptions.ScalacOptions

ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.13.11"
Test / tpolecatExcludeOptions += ScalacOptions.warnNonUnitStatement

// https://github.com/sbt/sbt/issues/2824
Test / fork := true

lazy val root = (project in file("."))
  .settings(scalacLocally)
  .settings(
    name := "medicover-sbot",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3"         %% "core"                  % "3.9.0",
      "com.softwaremill.sttp.client3"         %% "okhttp-backend"        % "3.9.0",
      "com.softwaremill.sttp.client3"         %% "slf4j-backend"         % "3.9.0",
      "ch.qos.logback"                         % "logback-classic"       % "1.4.11",
      "org.jsoup"                              % "jsoup"                 % "1.16.1",
      "com.github.pureconfig"                 %% "pureconfig"            % "0.17.4",
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % "2.23.3",
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.23.3" % Provided,
      "org.scalatest"                         %% "scalatest"             % "3.2.16" % Test
    )
  )

val scalacLocally: Seq[Def.Setting[?]] = {
  // disable Scala compiler errors locally, checking on some GitLabCI env
  if (!sys.env.contains("CI")) {
    Seq(scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused", "-Ywarn-unused-import"))
  } else {
    Seq()
  }
}
