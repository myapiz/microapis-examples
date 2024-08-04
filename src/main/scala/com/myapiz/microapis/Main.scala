package com.myapiz.microapis

import cats.effect.*
import cats.implicits.*
import com.comcast.ip4s.*
import com.myapiz.microapis.AuthMiddleware.AuthData
import com.myapiz.microapis.otp.{TimeBasedOTPServiceImpl, Service}
import org.http4s.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import smithy4s.http4s.SimpleRestJsonBuilder

object Routes {

  private val docs: HttpRoutes[IO] = smithy4s.http4s.swagger.docs[IO](Service)

  def getAll(local: IOLocal[Option[AuthData]]) = {
    val getAuthData: IO[AuthData] = local.get.flatMap {
      case Some(value) => IO.pure(value)
      case None =>
        IO.raiseError(
          new IllegalAccessException("Tried to access the value outside of the lifecycle of an http request")
        )
    }
    smithy4s.http4s.SimpleRestJsonBuilder
      .routes(new TimeBasedOTPServiceImpl(getAuthData))
      .middleware(AuthMiddleware(local))
      .resource
      .map(_ <+> docs)
  }

}

object Main extends IOApp.Simple {

  val run: IO[Unit] = IOLocal(Option.empty[AuthData]).flatMap { local =>
    Routes
      .getAll(local)
      .flatMap { routes =>
        EmberServerBuilder
          .default[IO]
          .withPort(port"9000")
          .withHost(host"0.0.0.0")
          .withHttpApp(routes.orNotFound)
          .build
      }
      .use(_ => IO.never)
  }
}
