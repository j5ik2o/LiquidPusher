package com.github.BambooTuna.LiquidPusher.pusher

object PusherProtocol {
  class PusherOnMessageError extends Exception

  case class SubscribeRequest(channel: String)

  trait ResponseJson
  case class EmptyJson()
  case class ConnectionResultResponseJson(data: String, event: String) extends ResponseJson

  case class ChannelConnectionResultResponseJson(channel: String, data: EmptyJson, event: String) extends ResponseJson
  case class ChannelResponseJson(channel: String, data: String, event: String) extends ResponseJson

  case class OtherResponseJson(data: String) extends ResponseJson

  trait OtherResponseProcess {
    def run(data: String, pusher: Pusher): Unit
    protected def convertMessageToObject(data: String): ResponseJson
  }
}
