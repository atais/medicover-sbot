import org.typelevel.scalacoptions.ScalacOptions

ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.13.11"
ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
ThisBuild / scalacOptions += "-Ymacro-annotations"

val common: Seq[Def.Setting[?]] = Seq(
  // https://github.com/sbt/sbt/issues/2824
  Test / tpolecatExcludeOptions += ScalacOptions.warnNonUnitStatement,
  Compile / tpolecatExcludeOptions += ScalacOptions.lintInferAny,
  Test / fork := true
)

val scalacLocally: Seq[Def.Setting[?]] = {
  // disable Scala compiler errors locally, checking on some GitLabCI env
  if (!sys.env.contains("CI")) {
    Seq(scalacOptions -= "-Xfatal-warnings")
  } else {
    Seq()
  }
}

lazy val cli = (project in file("cli"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio"       %% "zio"               % "2.0.16",
      "dev.zio"       %% "zio-macros"        % "2.0.16",
      "com.beachape"  %% "enumeratum"        % "1.7.3",
      "org.typelevel" %% "kittens"           % "3.0.0",
      "dev.zio"       %% "zio-test"          % "2.0.16" % Test,
      "dev.zio"       %% "zio-test-sbt"      % "2.0.16" % Test,
      "dev.zio"       %% "zio-test-magnolia" % "2.0.16" % Test
    )
  )
  .settings(common)
  .settings(scalacLocally)
  .dependsOn(medicover)

lazy val medicover = (project in file("medicover"))
  .settings(
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
  .settings(common)
  .settings(scalacLocally)

lazy val root = (project in file("."))
  .settings(
    name := "medicover-sbot"
  )
  .settings(common)
  .settings(scalacLocally)
  .aggregate(medicover, cli)
