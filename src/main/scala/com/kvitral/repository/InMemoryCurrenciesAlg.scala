package com.kvitral.repository

import cats.Applicative
import com.kvitral.algebras.CurrenciesAlg
import com.kvitral.model.Currency
import cats.syntax.all._

class InMemoryCurrenciesAlg[F[_]: Applicative](
    currenciesStore: Map[(Currency, Currency), BigDecimal])
    extends CurrenciesAlg[F] {

  override def convertCurrencies(from: Currency, to: Currency): F[Option[BigDecimal]] =
    currenciesStore.get((from, to)).pure[F]
}

object InMemoryCurrenciesAlg {
  def apply[F[_]: Applicative](
      currenciesStore: Map[(Currency, Currency), BigDecimal]): InMemoryCurrenciesAlg[F] =
    new InMemoryCurrenciesAlg(currenciesStore)
}
