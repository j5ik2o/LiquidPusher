package com.github.BambooTuna.LiquidPusher

import com.github.BambooTuna.LiquidPusher.client.LiquidPusher
import com.github.BambooTuna.LiquidPusher.pusher._

object Main {
  def main(args: Array[String]): Unit = {
    val pusherOptions = PusherOptions("wss://tap.liquid.com").setAuthorizer(PusherAuthorizer("key", "secret"))
    //val pusherOptions = PusherOptions("wss://tap.liquid.com", Some(PusherAuthorizer("key", "secret")))
    val liquidPusher = new LiquidPusher(pusherOptions)

    liquidPusher.connect(new ConnectionListener {
      override def onConnectedSucceeded(pusher: Pusher): Unit = {
        println("Connect Success")
        pusher.subscribe("price_ladders_cash_btcjpy_sell", new ChannelListener {
          override def onSubscriptionSucceeded(pusher: Pusher): Unit = println("Public success")
          override def onEvent(channelName: String, event: String, data: String): Unit =
            println(channelName + event)
          override def onError(e: Throwable): Unit = println(e.toString)
        })

        pusher.subscribePrivate("user_account_jpy_orders", new ChannelListener {
          override def onSubscriptionSucceeded(pusher: Pusher): Unit = println("Private success")
          override def onEvent(channelName: String, event: String, data: String): Unit =
            println(channelName + event + data)
          override def onError(e: Throwable): Unit = println(e.toString)
        })
      }
      override def onEvent(data: String): Unit = println(data)
      override def onClose(): Unit = println("close")
      override def onError(e: Throwable): Unit = println(e.toString)
    })

  }
}
