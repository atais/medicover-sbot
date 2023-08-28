ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name             := "medicover-sbot",
    idePackagePrefix := Some("com.github.atais"),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core"            % "3.9.0",
      "com.softwaremill.sttp.client3" %% "okhttp-backend"  % "3.9.0",
      "com.softwaremill.sttp.client3" %% "slf4j-backend"   % "3.9.0",
      "ch.qos.logback"                 % "logback-classic" % "1.4.11",
      "org.jsoup"                      % "jsoup"           % "1.16.1"
    )
  )
