package com.kvitral

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.effect.concurrent.Ref
import com.kvitral.endpoints.AccountEndpoint
import com.kvitral.model.{Account, RUB}
import com.kvitral.repository.{InMemoryAccountAlg, TaskLogger}
import com.kvitral.services.AccountService
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object Server {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val scheduler: Scheduler = monix.execution.Scheduler.global

  val initAccounts: Task[Ref[Task, Map[Long, Account]]] =
    Ref.of(Map[Long, Account]((1, Account(1L, 500d, RUB)), (2, Account(2L, 100d, RUB))))

  def main(args: Array[String]): Unit = {

    val appLogger = TaskLogger("Main")

    val program = for {
      _ <- appLogger.info("initializing storage:")
      initStorage <- initAccounts
      _ <- appLogger.info("initializing loggers:")
      inMemoryAccountLogger = TaskLogger("InMemoryAccountLogger")
      accountServiceLogger = TaskLogger("AccountService")
      _ <- appLogger.info("initializing algebras:")
      inMemoryAccountAlg = InMemoryAccountAlg[Task](initStorage, inMemoryAccountLogger)
      _ <- appLogger.info("initializing services:")
      accountService = AccountService[Task](inMemoryAccountAlg, accountServiceLogger)
      accountEndpoint = AccountEndpoint[Task](accountService)
      _ <- appLogger.info("starting server")
      route <- accountEndpoint.getAccountRoute
      _ <- appLogger.info("gettingRoutes")
      _ <- Task.deferFuture(Http().bindAndHandle(route, "localhost", 8080))
    } yield ()

    for (_ <- program.runToFuture)
      appLogger.info("serverStarted").runAsyncAndForget

    val promise = Promise[Unit]

    Await.result(promise.future, Duration.Inf)

  }
}
