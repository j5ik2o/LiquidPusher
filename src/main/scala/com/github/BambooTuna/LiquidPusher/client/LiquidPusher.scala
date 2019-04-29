package com.github.BambooTuna.LiquidPusher.client

import akka.actor.ActorSystem
import com.github.BambooTuna.LiquidPusher.client.LiquidPusherProtocol._
import com.github.BambooTuna.LiquidPusher.pusher.PusherProtocol._
import com.github.BambooTuna.LiquidPusher.pusher._
import com.github.BambooTuna.LiquidPusher.websocket.WebSocket
import pdi.jwt.{Jwt, JwtAlgorithm}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

class LiquidPusher(pusherOptions: PusherOptions)(implicit system: ActorSystem) extends Pusher(pusherOptions) {
  override protected val ws: WebSocket = new WebSocket(pusherOptions.host, setListener(this))

  private var authResultListenerOption: Option[ChannelListener] = None

  super.addOtherResponseProcess(otherResponseProcess())

  override def subscribePrivate(channelName: String, channelListener: ChannelListener): Unit = {
    pusherOptions.pusherAuthorizer.foreach(p => {
      authCheck(p, new ChannelListener {
        override def onSubscriptionSucceeded(pusher: Pusher): Unit = {
          pusher.subscribe(channelName, channelListener)
        }
        override def onEvent(channelName: String, event: String, data: String): Unit = {}
        override def onError(e: Throwable): Unit = logger.info(s"Error: connect failed, PrivateChannel: $channelName")
      })
    })
  }
  def otherResponseProcess(): OtherResponseProcess = {
    new OtherResponseProcess {
      override def run(data: String, pusher: Pusher): Unit = {
        convertMessageToObject(data) match {
          case AuthResultResponseJson(_, e) => separateAuthResult(e, pusher)
          case OtherResponseJson(d) => pusher.otherResponseProcessOption.foreach(_.run(d, pusher))
        }
      }

      private def separateAuthResult(event: String, pusher: Pusher): Unit = {
        authResultListenerOption.foreach(l => {
          if (event == "quoine:auth_success") l.onSubscriptionSucceeded(pusher)
          else l.onError(new AuthResultError)
        })
      }

      override protected def convertMessageToObject(data: String): ResponseJson = {
        parser.decode[AuthResultResponseJson](data)
          .getOrElse(OtherResponseJson(data))
      }
    }
  }

  private def authCheck(pusherAuthorizer: PusherAuthorizer, authResultListener: ChannelListener): Unit = {
    authResultListenerOption = Option(authResultListener)
    val data = AuthRequest("/realtime", Auth(createAuth(pusherAuthorizer))).asJson.noSpaces.replaceFirst("X_Quoine_Auth", "X-Quoine-Auth")
    send(createSendEventText("quoine:auth_request", data))
  }

  private def createAuth(pusherAuthorizer: PusherAuthorizer): String = {
    val text = AuthParameters(
      path = "/realtime",
      token_id = pusherAuthorizer.key
    ).asJson.noSpaces
    HMACSHA256(text, pusherAuthorizer.secret)
  }

  private def HMACSHA256(text: String, sharedSecret: String): String = {
    val algo = JwtAlgorithm.HS256
    Jwt.encode(text, sharedSecret, algo)
  }
}