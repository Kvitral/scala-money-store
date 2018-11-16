package com.kvitral.algebras

import com.kvitral.model.Currency

trait CurrenciesAlg[F[_]] {

  def convertCurrencies(from:Currency,to:Currency):F[Option[BigDecimal]]
}
