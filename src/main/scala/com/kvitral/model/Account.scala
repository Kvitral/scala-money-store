package com.kvitral.model

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class Account(id: Long, balance: BigDecimal, currency: Currency)

object Account {
  implicit val accountEncoder: Encoder[Account] = deriveEncoder[Account]
  implicit val accountDecoder: Decoder[Account] = deriveDecoder[Account]
}
