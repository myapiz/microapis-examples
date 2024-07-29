package com.myapiz.microapis

import cats.effect.*
import cats.implicits.*
import com.comcast.ip4s.*
import com.myapiz.microapis.otp.{OTPServiceImpl, Service}
import org.http4s.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import smithy4s.http4s.SimpleRestJsonBuilder

object Routes {
  protected[microapis] val otp: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(OTPServiceImpl).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](Service)

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
