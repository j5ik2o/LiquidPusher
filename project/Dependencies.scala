import sbt._

object Akka {
  private val version     = "2.5.19"
  val actor: ModuleID     = "com.typesafe.akka" %% "akka-actor" % version
  val stream: ModuleID    = "com.typesafe.akka" %% "akka-stream" % version
  val testkit: ModuleID   = "com.typesafe.akka" %% "akka-testkit" % version
  val slf4j: ModuleID     = "com.typesafe.akka" %% "akka-slf4j" % version
  private val httpVersion = "10.1.7"
  val http                = "com.typesafe.akka" %% "akka-http" % httpVersion
  val httpTestKit         = "com.typesafe.akka" %% "akka-http-testkit" % httpVersion
}

object Circe {
  private val version   = "0.11.1"
  val core: ModuleID    = "io.circe" %% "circe-core" % version
  val parser: ModuleID  = "io.circe" %% "circe-parser" % version
  val generic: ModuleID = "io.circe" %% "circe-generic" % version
  val extras: ModuleID  = "io.circe" %% "circe-generic-extras" % version

}

object Logback {
  private val version   = "1.2.3"
  val classic: ModuleID = "ch.qos.logback" % "logback-classic" % version
}

object LogstashLogbackEncoder {
  private val version = "4.11"
  val encoder = "net.logstash.logback" % "logstash-logback-encoder" % version excludeAll (
    ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-core"),
    ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-databind")
  )
}

object ScalaLogging {
  val version      = "3.5.0"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % version
}

object JWT {
  val core = "com.pauldijou" %% "jwt-core" % "2.1.0"
}