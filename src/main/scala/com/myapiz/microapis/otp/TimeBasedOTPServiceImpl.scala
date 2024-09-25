package com.myapiz.microapis.otp

import cats.effect.IO
import com.myapiz.smithy4s.middleware.AuthMiddleware.AuthData

import scala.concurrent.duration.*

class TimeBasedOTPServiceImpl(authData: IO[AuthData]) extends TOTP[IO] with OneTimePassword {

  override def generate(id: ID, ttl: Int, size: Int): IO[GenerateOutput] = for {
    data <- authData
    userID = s"${data.clientId}/${id.value}"
    result <- IO {
      generateTOTP(userID, ttl.seconds, size)
    }
  } yield GenerateOutput(Code(result))

  override def validate(id: ID, code: Code, ttl: Int): IO[ValidationOutput] = for {
    data <- authData
    userID = s"${data.clientId}/${id.value}"
    result <- IO {
      verifyTOTP(userID, ttl.seconds, code.value.length, code.value)
    }
  } yield ValidationOutput(result)
}
