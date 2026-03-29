package com.myapiz.microapis

import cats.data.Kleisli
import cats.effect.IO
import com.myapiz.smithy.error.NotAuthorizedError
import munit.CatsEffectSuite
import org.http4s.Response
import org.http4s.Method.POST
import org.http4s.Request
import org.http4s.Status
import org.http4s.implicits.*

class ServiceErrorHandlingTest extends CatsEffectSuite {

  test("maps NotAuthorizedError to 401") {
    val app = ServiceErrorHandling.httpApp(Kleisli[IO, Request[IO], Response[IO]] { _ =>
      IO.raiseError(NotAuthorizedError(title = "request not authorized with given permissions"))
    })

    app(Request[IO](method = POST, uri = uri"/top1")).flatMap { response =>
      IO {
        assertEquals(response.status, Status.Unauthorized)
      }
    }
  }
}
