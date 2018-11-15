package com.kvitral.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Transaction(from: Long, to: Long, amount: BigDecimal, currency: String)


object Transaction {
  implicit val accountEncoder: Encoder[Transaction] = deriveEncoder[Transaction]
  implicit val accountDecoder: Decoder[Transaction] = deriveDecoder[Transaction]
}