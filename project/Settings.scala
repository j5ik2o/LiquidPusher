import sbt.Keys._
import sbt._

object Settings {
  lazy val commonSettings = Seq(
    organization := "com.github.BambooTuna",
    publishTo := Some(Resolver.file("LiquidPusher",file("/C:/Users/user/Desktop/LiquidPusher"))(Patterns(true, Resolver.mavenStyleBasePattern))),
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      Logback.classic,
      LogstashLogbackEncoder.encoder,
    )
  )

}
