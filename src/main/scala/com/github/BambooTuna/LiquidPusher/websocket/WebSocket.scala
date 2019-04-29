package com.github.BambooTuna.LiquidPusher.websocket

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.{Done, NotUsed}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class WebSocket(val host: String, val wsListener: WebSocketListener)(implicit system: ActorSystem) {
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  private var wsInstance: Option[ActorRef] = None

  def wsRunner(): RunnableGraph[((ActorRef, Future[WebSocketUpgradeResponse]), NotUsed)] = {
    val req = WebSocketRequest(host)
    val webSocketFlow = Http().webSocketClientFlow(req)
    val messageSource: Source[Message, ActorRef] =
      Source.actorRef[TextMessage.Strict](bufferSize = 10, OverflowStrategy.fail)
    val messageSink: Sink[Message, NotUsed] = Flow[Message]
      .map{
        case TextMessage.Strict(m) => wsListener.onMessage(m)
        case BinaryMessage.Strict(m) => wsListener.onMessage(m.utf8String)
        case _ => wsListener.onError(new Exception("Get Wrong Message"))
      }
      .to(Sink.ignore)
    messageSource
      .viaMat(webSocketFlow)(Keep.both)
      .toMat(messageSink)(Keep.both)
  }

  def connect(): Unit = {
    val ((ws, upgradeResponse), _) = wsRunner().run()
    setWsInstance(ws)
    val connected = upgradeResponse.flatMap { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Future.successful(Done)
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }
    connected.onComplete {
      case Success(_) => wsListener.onConnectedSucceeded(this)
      case Failure(exception) => wsListener.onError(exception)
    }
  }

  def send(data: String): Unit = {
    wsInstance.foreach(_ ! TextMessage.Strict(data))
  }

  private def setWsInstance(ws: ActorRef): Unit = {
    wsInstance = Option(ws)
  }

}