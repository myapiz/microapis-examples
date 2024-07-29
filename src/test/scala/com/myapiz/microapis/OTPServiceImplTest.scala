package com.myapiz.microapis

import cats.effect.unsafe.IORuntime
import com.myapiz.microapis.otp.OneTimePassword
import munit.FunSuite

import scala.concurrent.duration.*

implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

class OTPServiceImplTest extends FunSuite {

  private val service = new OneTimePassword {}
  test("generate OTP") {
    val id = "id"
    val result = service.generateTOTP(id, 1.seconds, 6)
    Thread.sleep(1 * 1000)
    val result2 = service.generateTOTP(id, 1.seconds, 6)
    assertEquals(result.length, 6)
    assertNotEquals(result, result2)
  }

  test("validate OTP") {
    val id = "id"
    val result = service.generateTOTP(id, 1.seconds, 6)
    assertEquals(service.verifyTOTP(id, 1.seconds, 6, result), true)
  }

}
