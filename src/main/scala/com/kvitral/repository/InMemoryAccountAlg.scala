package com.kvitral.repository

import cats.Monad
import cats.effect.concurrent.Ref
import cats.syntax.all._
import com.kvitral.algebras.{AccountAlg, Logging}
import com.kvitral.model.errors.{AccountNotFound, AccountServiceErrors, InsufficientBalance}
import com.kvitral.model.{Account, Transaction}

import scala.language.higherKinds

class InMemoryAccountAlg[F[_]: Monad](accountState: Ref[F, Map[Long, Account]], logger: Logging[F])
    extends AccountAlg[F] {

  override def getAccount(i: Long): F[Option[Account]] =
    for {
      account <- accountState.get
    } yield account.get(i)

  override def changeBalance(transaction: Transaction): F[Either[AccountServiceErrors, Unit]] =
    for {
      trResult <- accountState.modify { accState =>
        accState
          .get(transaction.from)
          .fold((accState, Either.left[AccountServiceErrors, Unit](AccountNotFound))) { accFrom =>
            if (accFrom.balance < transaction.amount)
              (accState, Either.left[AccountServiceErrors, Unit](InsufficientBalance))
            else {
              accState
                .get(transaction.to)
                .fold(accState, Either.left[AccountServiceErrors, Unit](AccountNotFound)) { accTo =>
                  val res = accState
                    .updated(
                      accFrom.id,
                      accFrom.copy(balance = accFrom.balance - transaction.amount))
                    .updated(accTo.id, accTo.copy(balance = accTo.balance + transaction.amount))
                  (res, Either.right[AccountServiceErrors, Unit](()))
                }
            }
          }
      }
      _ <- logger.info(s"result of transaction is ${trResult.toString}")
    } yield trResult

}

object InMemoryAccountAlg {
  def apply[F[_]: Monad](
      accountState: Ref[F, Map[Long, Account]],
      logger: Logging[F]): InMemoryAccountAlg[F] =
    new InMemoryAccountAlg(accountState, logger)
}
