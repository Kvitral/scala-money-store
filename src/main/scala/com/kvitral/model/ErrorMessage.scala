package com.kvitral.model

import com.kvitral.model.errors.ServiceErrors
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto._

case class ErrorMessage(message: String, errorType: ServiceErrors)


object ErrorMessage {
  private implicit val conf = Configuration.default.withDiscriminator("errorType")
  implicit val encoder: Encoder[ErrorMessage] = deriveEncoder
  implicit val decoder: Decoder[ErrorMessage] = deriveDecoder
}
