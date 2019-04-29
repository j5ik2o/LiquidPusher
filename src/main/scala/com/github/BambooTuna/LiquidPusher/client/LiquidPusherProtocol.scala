package com.github.BambooTuna.LiquidPusher.client

import com.github.BambooTuna.LiquidPusher.pusher.PusherProtocol.{EmptyJson, ResponseJson}

object LiquidPusherProtocol {
  class AuthResultError extends Exception

  case class ApiKey(key: String, secret: String)

  case class AuthParameters(path: String, nonce: Long = System.currentTimeMillis, token_id: String)

  case class Auth(X_Quoine_Auth: String)
  case class AuthRequest(path: String, headers: Auth)

  case class AuthResultResponseJson(data: EmptyJson, event:String) extends ResponseJson
}
