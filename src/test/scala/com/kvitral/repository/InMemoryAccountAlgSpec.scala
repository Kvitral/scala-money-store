package com.kvitral.repository

import akka.http.scaladsl.testkit.ScalatestRouteTest
import cats.effect.concurrent.Ref
import com.kvitral.model.errors.{AccountServiceErrors, InsufficientBalance}
import com.kvitral.model.{Account, RUB, Transaction}
import com.kvitral.utils.TaskRouteTest
import monix.eval.Task
import org.scalatest.{FlatSpec, Matchers}

class InMemoryAccountAlgSpec
    extends FlatSpec
    with Matchers
    with ScalatestRouteTest
    with TaskRouteTest {

  trait mix {
    val initMap: Map[Long, Account] = Map(
      (1, Account(1L, 500d, RUB)),
      (2, Account(2L, 100d, RUB)),
      (3, Account(3L, 200d, RUB))
    )
    val initRef: Task[Ref[Task, Map[Long, Account]]] = Ref.of(initMap)

    def getInMemoryAccount(store: Ref[Task, Map[Long, Account]]): Task[InMemoryAccountAlg[Task]] =
      for {
        _ <- Task.unit
        inMemoryLogger = TaskLogger("InMemoryTest")
      } yield InMemoryAccountAlg[Task](store, inMemoryLogger)

    /*

      using Task.gather which will gives us parallel task execution but will keep order of results

     */
    def concurrentUpdates(inMemoryAccountAlg: InMemoryAccountAlg[Task])
      : Task[List[Either[AccountServiceErrors, Unit]]] = {
      val t12 = Transaction(1, 2, 200, RUB)
      val t23 = Transaction(2, 3, 400, RUB)
      val t31 = Transaction(3, 1, 100, RUB)

      val trList = List(t12, t23, t31).map(inMemoryAccountAlg.changeBalance)

      Task.gather(trList)
    }

    val effectsAfterUpdate = List(Right(()), Left(InsufficientBalance), Right(()))

    val amountsMapAfterUpdates: Map[Long, BigDecimal] = Map(1L -> 400d, 2L -> 300d, 3L -> 100d)
  }

  "InMemoryAccountAlg.changeBalance" should "change accounts balances" in new mix {
    val t = Transaction(1, 2, BigDecimal(200.25), RUB)
    runTask(for {
      store <- initRef
      inmemory <- getInMemoryAccount(store)
      res <- inmemory.changeBalance(t)
      changedStore <- store.get
    } yield {
      res shouldEqual Right(())
      changedStore.get(t.from) shouldEqual initMap
        .get(t.from)
        .map(a => a.copy(balance = a.balance - t.amount))
      changedStore
        .get(t.to) shouldEqual initMap.get(t.to).map(a => a.copy(balance = a.balance + t.amount))
    })
  }

  it should "return InsufficientBalance if account doesn`t have amount on balance" in new mix {
    val t = Transaction(1, 2, BigDecimal(600), RUB)
    runTask(for {
      store <- initRef
      inmemory <- getInMemoryAccount(store)
      res <- inmemory.changeBalance(t)
      changedStore <- store.get
    } yield {
      res shouldEqual Left(InsufficientBalance)
    })
  }

  it should "handle concurrent updates" in new mix {
    runTask(for {
      store <- initRef
      inmemory <- getInMemoryAccount(store)
      effects <- concurrentUpdates(inmemory)
      changedStore <- store.get
    } yield {
      effects shouldEqual effectsAfterUpdate
      changedStore
        .map { case (k, v) => k -> v.balance } shouldEqual amountsMapAfterUpdates
    })
  }

  "InMemoryAccountAlg.getBalance" should "return existing account" in new mix {
    val accNumber = 1L
    runTask(for {
      store <- initRef
      inmemory <- getInMemoryAccount(store)
      account <- inmemory.getAccount(accNumber)
    } yield {
      account shouldEqual initMap.get(accNumber)
    })
  }

  it should "return None if account is missing" in new mix {
    val accNumber = 0
    runTask(for {
      store <- initRef
      inmemory <- getInMemoryAccount(store)
      account <- inmemory.getAccount(accNumber)
    } yield {
      account shouldEqual None
    })
  }

}
