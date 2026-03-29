package com.myapiz.microapis

import cats.data.Kleisli
import cats.effect.*
import cats.implicits.*
import com.comcast.ip4s.*
import com.myapiz.microapis.otp.{TOTP, TimeBasedOTPServiceImpl}
import com.myapiz.smithy.error.{NotAuthenticatedError, NotAuthorizedError}
import com.myapiz.smithy4s.middleware.*
import com.myapiz.smithy4s.middleware.AuthMiddleware.AuthData
import io.circe.Json
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.server.middleware.{ErrorHandling, Logger, RequestId}
import smithy4s.interopcats.monoidEndpointMiddleware

object ServiceErrorHandling {

  private def safeTitle(title: String, fallback: String): String =
    Option(title).filter(_.nonEmpty).getOrElse(fallback)

  private def problemResponse(
    statusCode: Int,
    title: String,
    detail: Option[String],
    problemType: Option[String],
    instance: Option[String]
  ): IO[Response[IO]] = {
    val status = Status.fromInt(statusCode).getOrElse(Status.InternalServerError)
    val fields = List(
      Some("title" -> Json.fromString(title)),
      Some("status" -> Json.fromInt(status.code)),
      problemType.map("type" -> Json.fromString(_)),
      detail.filter(_.nonEmpty).map("detail" -> Json.fromString(_)),
      instance.map("instance" -> Json.fromString(_))
    ).flatten

    IO.pure(Response[IO](status = status).withEntity(Json.obj(fields*)))
  }

  def httpApp(app: HttpApp[IO]): HttpApp[IO] =
    Kleisli { req =>
      app(req).handleErrorWith {
        case error: NotAuthenticatedError =>
          problemResponse(
            statusCode = error.status(),
            title = safeTitle(error.title(), "not authenticated"),
            detail = error.detail().toList.headOption,
            problemType = error._type().toList.headOption,
            instance = error.instance().toList.headOption
          )
        case error: NotAuthorizedError =>
          problemResponse(
            statusCode = error.status(),
            title = safeTitle(error.title(), "not authorized"),
            detail = error.detail().toList.headOption,
            problemType = error._type().toList.headOption,
            instance = error.instance().toList.headOption
          )
      }
    }
}

object Routes {

  private val docs: HttpRoutes[IO] = smithy4s.http4s.swagger.docs[IO](TOTP)

  def getAll(local: IOLocal[Option[AuthData]]): Resource[IO, HttpRoutes[IO]] = {
    val getAuthData: IO[AuthData] = local.get.flatMap {
      case Some(value) => IO.pure(value)
      case None =>
        IO.raiseError(
          new IllegalAccessException("Tried to access the value outside of the lifecycle of an http request")
        )
    }
    smithy4s.http4s.SimpleRestJsonBuilder
      .routes(new TimeBasedOTPServiceImpl(getAuthData))
      .middleware(
        AuthzMiddleware(local) |+| AuthMiddleware(local) |+| Http4sMiddleware(
          ErrorHandling.httpApp
        ) |+| Http4sMiddleware(Logger.httpApp(logHeaders = true, logBody = false)) |+| Http4sMiddleware(
          RequestId.httpApp.apply
        )
      )
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
          .withHttpApp(ServiceErrorHandling.httpApp(routes.orNotFound))
          .build
      }
      .use(_ => IO.never)
  }
}
