package com.myapiz.microapis

import cats.effect.{IO, IOLocal}
import com.myapiz.microapis.otp.NotAuthorizedError
import io.circe.Decoder
import io.circe.parser.decode
import org.http4s.HttpApp
import org.typelevel.ci.CIString
import smithy4s.Hints
import smithy4s.http4s.ServerEndpointMiddleware

object AuthMiddleware {

  case class AuthData(clientId: String, email: String, scope: String)

  private val anAuthDataDecoder: Decoder[AuthData] =
    Decoder.forProduct3("clientId", "email", "scope")(AuthData.apply)

  private def decodeAuthData(encodedData: String) = {
    decode[AuthData](encodedData)(using anAuthDataDecoder)
      .fold(err => IO.raiseError(new NotAuthorizedError(s"Invalid AuthData: ${err}")), data => IO.pure(data))
  }

  private def middleware(headerName: String, local: IOLocal[Option[AuthData]]): HttpApp[IO] => HttpApp[IO] = {
    inputApp =>
      HttpApp[IO] { request => // 3
        val maybeKey = request.headers
          .get(CIString(headerName))
          // first header value
          .map(_.head.value)
          // is decoded to AuthData
          .map(decodeAuthData)
          .getOrElse(IO.raiseError(new NotAuthorizedError(s"Missing AuthData in ${headerName}")))

        for {
          data <- maybeKey
          _ <- local.set(Some(data))
          response <- inputApp(request)
        } yield response
      }
  }

  def apply(local: IOLocal[Option[AuthData]]): ServerEndpointMiddleware[IO] =
    new ServerEndpointMiddleware.Simple[IO] {

      def prepareWithHints(serviceHints: Hints, endpointHints: Hints): HttpApp[IO] => HttpApp[IO] = {
        val hint = endpointHints.get[smithy.api.HttpApiKeyAuth].orElse(serviceHints.get[smithy.api.HttpApiKeyAuth])
        hint match {
          case Some(serviceHint) => middleware(serviceHint.name.toString, local)
          case None              => identity
        }
      }
    }
}
