package com.kvitral.services

import cats.Monad
import cats.data.EitherT
import cats.syntax.all._
import com.kvitral.algebras.{AccountAlg, Logging}
import com.kvitral.model.errors.{AccountNotFound, AccountServiceErrors}
import com.kvitral.model.{Account, Transaction}

import scala.language.higherKinds

class AccountService[F[_] : Monad](accRepo: AccountAlg[F], logger: Logging[F]) {

  def getAccount(id: Long): EitherT[F, AccountNotFound.type, Account] =
    EitherT.fromOptionF(
      for {
        _ <- logger.info(s"getting account for $id")
        account <- accRepo.getAccount(id)
      } yield account,
      AccountNotFound)

  def changeBalance(transaction: Transaction): EitherT[F, AccountServiceErrors, Unit] = {
    for {
      _ <- logger.info(s"changing balance with transaction $transaction")
    } yield ()

    accRepo.changeBalance(transaction)
  }

}

object AccountService {
  def apply[F[_] : Monad](accRepo: AccountAlg[F], logger: Logging[F]): AccountService[F] =
    new AccountService(accRepo, logger)
}
