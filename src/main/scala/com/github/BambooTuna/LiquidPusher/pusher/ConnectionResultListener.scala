package com.github.BambooTuna.LiquidPusher.pusher

trait ConnectionResultListener extends PusherListener {
  def onConnectedSucceeded(pusher: Pusher): Unit
}
