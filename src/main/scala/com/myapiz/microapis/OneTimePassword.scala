package com.myapiz.microapis

import com.bastiaanjansen.otp.{HMACAlgorithm, TOTPGenerator}

import scala.concurrent.duration.FiniteDuration
import scala.jdk.javaapi.DurationConverters.toJava

trait OneTimePassword {

  private def secret(id: String) = {
    s"my-special-$id-secret".getBytes
  }

  def generate(id: String, ttl: FiniteDuration, size: Int): String = {
    val totp = TOTPGenerator
      .Builder(secret(id))
      .withHOTPGenerator(builder => {
        builder.withPasswordLength(size)
        builder.withAlgorithm(HMACAlgorithm.SHA256) // SHA256 and SHA512 are also supported
        ()
      })
      .withPeriod(toJava(ttl))
      .build()

    totp.now
  }
  def verify(id: String, code: String): Boolean = {
    val totp = TOTPGenerator
      .Builder(secret(id))
      .build()

    totp.verify(code)
  }

}
