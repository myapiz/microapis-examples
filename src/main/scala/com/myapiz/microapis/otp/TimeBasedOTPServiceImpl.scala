package com.myapiz.microapis.otp

import cats.effect.IO
import com.myapiz.microapis.AuthMiddleware.AuthData

import scala.concurrent.duration.*

class TimeBasedOTPServiceImpl(authData: IO[AuthData]) extends Service[IO] with OneTimePassword {

  override def generate(id: ID, ttl: Int, size: Int): IO[OTP] = for {
    data <- authData
    userID = s"${data.clientId}/${id.value}"
    result <- IO {
      generateTOTP(userID, ttl.seconds, size)
    }
  } yield OTP(Code(result))

  override def validate(id: ID, code: Code, ttl: Int): IO[ValidationResponse] = for {
    data <- authData
    userID = s"${data.clientId}/${id.value}"
    result <- IO {
      verifyTOTP(userID, ttl.seconds, code.value.length, code.value)
    }
  } yield ValidationResponse(result)
}
