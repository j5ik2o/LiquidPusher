package com.github.BambooTuna.LiquidPusher.pusher


trait PusherListener

trait ConnectionListener extends PusherListener {
  def onConnectedSucceeded(pusher: Pusher): Unit
  def onEvent(data: String): Unit
  def onClose(): Unit
  def onError(e: Throwable): Unit
}

trait ChannelListener extends PusherListener {
  def onSubscriptionSucceeded(pusher: Pusher): Unit
  def onEvent(channelName: String, event: String, data: String): Unit
  def onError(e: Throwable): Unit
}



