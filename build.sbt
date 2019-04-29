import Settings._

lazy val Sample =
  (project in file("sample"))
    .settings(commonSettings)
    .settings(
      name := "liquidpusher-sample",
      libraryDependencies ++= Seq(
        Circe.core,
        Circe.generic,
        Circe.parser,
        Akka.http,
        Akka.stream,
        Akka.slf4j,
        JWT.core
      )
    )
  .dependsOn(root)

lazy val root =
  (project in file("."))
    .settings(commonSettings)
    .settings(
      name := "liquidpusher",
      libraryDependencies ++= Seq(
        Circe.core,
        Circe.generic,
        Circe.parser,
        Akka.http,
        Akka.stream,
        Akka.slf4j,
        JWT.core
      )
    )