package com.kvitral.model.errors

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import io.circe.{Decoder, Encoder}

sealed trait ServiceErrors extends Product with Serializable

sealed trait AccountServiceErrors extends Product with Serializable with ServiceErrors

case object AccountNotFound extends AccountServiceErrors

case object InsufficientBalance extends AccountServiceErrors

sealed trait CurrenciesServiceErrors extends Product with Serializable with ServiceErrors

case object CurrencyNotFound extends CurrenciesServiceErrors

object ServiceErrors {
  private implicit val conf: Configuration = Configuration.default
  implicit val serviceErrorEncoder: Encoder[ServiceErrors] = deriveEnumerationEncoder
  implicit val serviceErrorDecoder: Decoder[ServiceErrors] = deriveEnumerationDecoder
  implicit val accountServiceErrorEncoder: Encoder[AccountServiceErrors] = deriveEnumerationEncoder
  implicit val accountServiceErrorDecoder: Decoder[AccountServiceErrors] = deriveEnumerationDecoder
  implicit val currenciesServiceErrorEncoder: Encoder[CurrenciesServiceErrors] = deriveEnumerationEncoder
  implicit val currenciesServiceErrorDecoder: Decoder[CurrenciesServiceErrors] = deriveEnumerationDecoder
}
