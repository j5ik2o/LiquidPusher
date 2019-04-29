package com.github.BambooTuna.LiquidPusher.pusher

import akka.actor.ActorSystem
import com.github.BambooTuna.LiquidPusher.LogSupport
import com.github.BambooTuna.LiquidPusher.pusher.PusherProtocol._
import com.github.BambooTuna.LiquidPusher.websocket.{WebSocket, WebSocketListener}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

class Pusher(pusherOptions: PusherOptions)(implicit system: ActorSystem) extends LogSupport {
  protected var connectionListenerOption: Option[ConnectionListener] = None
  protected var channelListeners: Map[String, ChannelListener] = Map.empty

  var otherResponseProcessOption: Option[OtherResponseProcess] = None

  protected val ws = new WebSocket(pusherOptions.host, setListener(this))

  def connect(connectionListener: ConnectionListener): Unit = {
    connectionListenerOption = Option(connectionListener)
    ws.connect()
  }

  final def subscribe(channelName: String): Unit = {
    send(createSendEventText("pusher:subscribe", SubscribeRequest(channelName).asJson.noSpaces))
  }

  final def subscribe(channelName: String, channelListener: ChannelListener): Unit = {
    bind(channelName, channelListener)
    subscribe(channelName)
  }

  def subscribePrivate(channelName: String, channelListener: ChannelListener): Unit = {
    pusherOptions.pusherAuthorizer.foreach(_ => {
      bind(channelName, channelListener)
      subscribe(channelName)
    })
  }

  final def bind(channelName: String, channelListener: ChannelListener): Unit = {
    channelListeners = channelListeners + (channelName -> channelListener)
  }

  final def addOtherResponseProcess(otherResponseProcess: OtherResponseProcess): Unit = {
    otherResponseProcessOption = Option(otherResponseProcess)
  }

  protected def setListener(pusher: Pusher): WebSocketListener = new WebSocketListener {
    override def onConnectedSucceeded(ws: WebSocket): Unit = {}
    override def onMessage(data: String): Unit = {
      bindMessageToListener(data, pusher)
    }
    override def onClose(): Unit = pusher.connectionListenerOption.foreach(_.onClose())
    override def onError(e: Throwable): Unit = pusher.connectionListenerOption.foreach(_.onError(e))
  }

  protected def bindMessageToListener(data: String, pusher: Pusher): Unit = {
    convertMessageToObject(data) match {
      case ChannelResponseJson(c, d, e) => pusher.channelListeners.get(c).foreach(_.onEvent(c, e, d))
      case ChannelConnectionResultResponseJson(c, _, e)  => separateChannelConnectionResult(c, e, pusher)
      case ConnectionResultResponseJson(_, e) => separateConnectionResult(e, pusher)
      case OtherResponseJson(d) => pusher.otherResponseProcessOption.foreach(_.run(d, pusher))
    }
  }

  protected def separateChannelConnectionResult(channelName: String, event: String, pusher: Pusher): Unit = {
    pusher.channelListeners.get(channelName).foreach(l => {
      if (event == "pusher_internal:subscription_succeeded") l.onSubscriptionSucceeded(pusher)
      else l.onError(new PusherOnMessageError)
    })
  }

  protected def separateConnectionResult(event: String, pusher: Pusher): Unit = {
    pusher.connectionListenerOption.foreach(l => {
      if (event == "pusher:connection_established") l.onConnectedSucceeded(pusher)
      else l.onError(new PusherOnMessageError)
    })
  }

  protected def convertMessageToObject(data: String): ResponseJson = {
    parser.decode[ChannelResponseJson](data)
      .left.flatMap(_ => parser.decode[ChannelResponseJson](data))
      .left.flatMap(_ => parser.decode[ChannelConnectionResultResponseJson](data))
      .left.flatMap(_ => parser.decode[ConnectionResultResponseJson](data))
      .getOrElse(OtherResponseJson(data))
  }

  protected def createSendEventText(event: String, data: String): String = {
    s"""{"event":"$event","data":$data}"""
  }

  protected def send(data: String): Unit = {
    ws.send(data)
  }
}