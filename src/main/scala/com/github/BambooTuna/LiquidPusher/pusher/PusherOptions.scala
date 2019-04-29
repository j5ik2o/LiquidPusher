package com.github.BambooTuna.LiquidPusher.pusher

case class PusherOptions(host: String, pusherAuthorizer: Option[PusherAuthorizer] = None) {
  def setHost(host: String): PusherOptions = copy(host = host)
  def setAuthorizer(pusherAuthorizer: PusherAuthorizer): PusherOptions = copy(pusherAuthorizer = Option(pusherAuthorizer))
}
