package com.kvitral.algebras

import cats.data.EitherT
import com.kvitral.model.errors.AccountServiceErrors
import com.kvitral.model.{Account, Transaction}

import scala.language.higherKinds

trait AccountAlg[F[_]] {
  def getAccount(i: Long): F[Option[Account]]

  def changeBalance(transaction: Transaction): EitherT[F, AccountServiceErrors, Unit]

}
