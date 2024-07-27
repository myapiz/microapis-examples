package com.myapiz.microapis

import cats.effect.*
import cats.implicits.*
import com.comcast.ip4s.*
import com.myapiz.microapis.otp.{OTP, OneTimePasswordService, ValidationResponse}
import org.http4s.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import smithy4s.http4s.SimpleRestJsonBuilder

import scala.concurrent.duration.*
object OTPServiceImpl extends OneTimePasswordService[IO] with OneTimePassword {

  override def generate(id: String, ttl: Option[Int], size: Option[Int]): IO[OTP] = IO {
    OTP(generate(id, ttl.getOrElse(60).seconds, size.getOrElse(6)))
  }

  override def validate(id: String, code: String): IO[ValidationResponse] = IO {
    ValidationResponse(verify(id, code))
  }
}

object Routes {
  private val otp: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(OTPServiceImpl).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](OneTimePasswordService)

  val all: Resource[IO, HttpRoutes[IO]] = otp.map(_ <+> docs)
}

object Main extends IOApp.Simple {

  val run: IO[Unit] = Routes.all
    .flatMap { routes =>
      EmberServerBuilder
        .default[IO]
        .withPort(port"9000")
        .withHost(host"localhost")
        .withHttpApp(routes.orNotFound)
        .build
    }
    .use(_ => IO.never)

}
