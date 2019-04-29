package com.github.BambooTuna.LiquidPusher.websocket

trait WebSocketListener {
  def onConnectedSucceeded(ws: WebSocket): Unit
  def onMessage(data: String): Unit
  def onClose(): Unit
  def onError(e: Throwable): Unit
}
