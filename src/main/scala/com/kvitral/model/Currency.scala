package com.kvitral.model

import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto._

sealed trait Currency extends Product with Serializable

case object EUR extends Currency

case object RUB extends Currency

case object USD extends Currency

object Currency {
  private implicit val config: Configuration = Configuration.default
  implicit val currencyEncoder: Encoder[Currency] = deriveEnumerationEncoder[Currency]
  implicit val currencyDecoder: Decoder[Currency] = deriveEnumerationDecoder[Currency]
}
