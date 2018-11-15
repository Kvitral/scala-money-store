package com.kvitral.endpoints

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import cats.Monad
import cats.syntax.all._
import com.kvitral.model.{ErrorMessage, Transaction}
import com.kvitral.services.AccountService
import com.kvitral.transformers.EffectToRoute
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.language.higherKinds

class AccountEndpoint[F[_]: Monad: EffectToRoute](accountService: AccountService[F]) {

  private val effectToRoute: EffectToRoute[F] = implicitly[EffectToRoute[F]]

  val getAccountRoute: F[Route] = (path("getAccounts") {
    get {
      parameters('accountId.as[Long]) { id =>
        val res = accountService
          .getAccount(id)
          .value
          .map(_.left.map(err => ErrorMessage(s"Couldn`t find account with id $id", err)))
        effectToRoute.toRoute(res)
      }
    }
  } ~ path("transfer") {
    post {
      entity(as[Transaction]) { tr =>
        val res = accountService
          .changeBalance(tr)
          .map(
            _.left
              .map(err => ErrorMessage("something went wrong", err))
              .map(_ => "OK"))

        effectToRoute.toRoute(res)
      }
    }
  }).pure[F]

}

object AccountEndpoint {
  def apply[F[_]: Monad: EffectToRoute](accountService: AccountService[F]): AccountEndpoint[F] =
    new AccountEndpoint(accountService)
}
