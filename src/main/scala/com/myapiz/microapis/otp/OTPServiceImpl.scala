package com.myapiz.microapis.otp

import scala.concurrent.duration.*

import cats.effect.IO

object OTPServiceImpl extends Service[IO] with OneTimePassword {

  private val defaultTTL = 60.seconds
  private val defaultSize = 6

  override def generate(id: ID, ttl: Option[Int], size: Option[Int]): IO[OTP] = IO {
    OTP(Code(generateTOTP(id.value, ttl.map(_.seconds).getOrElse(defaultTTL), size.getOrElse(defaultSize))))
  }

  override def validate(id: ID, code: Code, ttl: Option[Int]): IO[ValidationResponse] = IO {
    ValidationResponse(verifyTOTP(id.value, ttl.map(_.seconds).getOrElse(defaultTTL), code.value.length, code.value))
  }
}
