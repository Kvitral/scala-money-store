package com.kvitral.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Transaction(from: Long, to: Long, amount: BigDecimal, currency: String)

object Transaction {
  implicit val accountEncoder: Encoder[Transaction] = deriveEncoder[Transaction]
  implicit val accountDecoder: Decoder[Transaction] = deriveDecoder[Transaction]
}
