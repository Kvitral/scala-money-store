package com.kvitral

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.effect.concurrent.Ref
import com.kvitral.endpoints.AccountEndpoint
import com.kvitral.model._
import com.kvitral.repository.{InMemoryAccountAlg, InMemoryCurrenciesAlg, TaskLogger}
import com.kvitral.services.{AccountService, CurrenciesService}
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

  val currenciesMap: Map[(Currency, Currency), BigDecimal] = Map(
    (RUB, EUR) -> 0.013,
    (EUR, RUB) -> 74.90,
    (USD, RUB) -> 66.1,
    (RUB, USD) -> 0.015,
    (EUR, USD) -> 1.13,
    (USD, EUR) -> 0.88,
    (EUR, EUR) -> 1,
    (RUB, RUB) -> 1,
    (USD, USD) -> 1
  )

  def main(args: Array[String]): Unit = {

    val appLogger = TaskLogger("Main")

    val program = for {
      _ <- appLogger.info("initializing storage:")
      initAccountsStorage <- initAccounts
      initCurrenciesStorage <- Task.eval(currenciesMap)
      _ <- appLogger.info("initializing loggers:")
      inMemoryAccountLogger = TaskLogger("InMemoryAccountLogger")
      accountServiceLogger = TaskLogger("AccountService")
      currencyServiceLogger = TaskLogger("CurrencyService")
      _ <- appLogger.info("initializing algebras:")
      inMemoryAccountAlg = InMemoryAccountAlg[Task](initAccountsStorage, inMemoryAccountLogger)
      inMemoryCurrencyAlg = InMemoryCurrenciesAlg[Task](initCurrenciesStorage)
      _ <- appLogger.info("initializing services:")
      curencyService = CurrenciesService[Task](inMemoryCurrencyAlg, currencyServiceLogger)
      accountService = AccountService[Task](
        inMemoryAccountAlg,
        accountServiceLogger,
        curencyService)
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
