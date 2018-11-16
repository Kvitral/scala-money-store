package com.kvitral.services

import cats.Monad
import cats.syntax.all._
import com.kvitral.algebras.{CurrenciesAlg, Logging}
import com.kvitral.model.Currency
import com.kvitral.model.errors.{CurrenciesServiceErrors, CurrencyNotFound}

class CurrenciesService[F[_]: Monad](currencyRepo: CurrenciesAlg[F], logger: Logging[F]) {

  def getCurrencyRate(
      from: Currency,
      to: Currency): F[Either[CurrenciesServiceErrors, BigDecimal]] =
    for {
      rate <- currencyRepo.convertCurrencies(from, to)
      _ <- logger.info(s"rate for $from to $to is $rate")
    } yield rate.toRight(CurrencyNotFound)

}

object CurrenciesService {
  def apply[F[_]: Monad](currencyRepo: CurrenciesAlg[F], logger: Logging[F]): CurrenciesService[F] =
    new CurrenciesService(currencyRepo, logger)
}
