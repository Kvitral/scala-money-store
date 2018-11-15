package com.kvitral.transformers

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import cats.Id
import monix.eval.Task
import monix.execution.Scheduler

trait EffectToRoute[F[_]] {
  def toRoute[A: ToResponseMarshaller](fa: F[A]): StandardRoute
}

object EffectToRoute {
  //  def apply[F[_], A: ToResponseMarshaller]: EffectToRoute[F] = implicitly[EffectToRoute[F]]

  implicit def toRoute[F[_], A: ToResponseMarshaller](
      implicit effectToRoute: EffectToRoute[F]): EffectToRoute[F] = effectToRoute

  implicit def convertTask(implicit s: Scheduler): EffectToRoute[Task] = new EffectToRoute[Task] {
    override def toRoute[A: ToResponseMarshaller](fa: Task[A]): StandardRoute =
      complete(fa.runToFuture(s))
  }

  implicit val convertId: EffectToRoute[Id] = new EffectToRoute[Id] {
    override def toRoute[A: ToResponseMarshaller](fa: Id[A]): StandardRoute = complete(fa)
  }
}
