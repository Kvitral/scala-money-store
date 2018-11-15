package com.kvitral.repository

import cats.Id
import com.kvitral.algebras.Logging
import org.slf4j.LoggerFactory

class IdLogger(name: String) extends Logging[Id] {
  val logger = LoggerFactory.getLogger(name)

  override def info(msg: => String): Id[Unit] = logger.info(msg)

  override def error(msg: => String): Id[Unit] = logger.info(msg)
}

object IdLogger {
  def apply(name: String): IdLogger = new IdLogger(name)
}
