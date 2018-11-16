package com.kvitral.services

import akka.http.scaladsl.testkit.ScalatestRouteTest
import cats.effect.concurrent.Ref
import com.kvitral.model.errors.AccountNotFound
import com.kvitral.model.{Account, RUB}
import com.kvitral.repository.{InMemoryAccountAlg, TaskLogger}
import com.kvitral.utils.TaskRouteTest
import monix.eval.Task
import org.scalatest.{FlatSpec, Matchers}

class AccountServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with TaskRouteTest {

  trait mix {
    val initMap: Map[Long, Account] = Map(
      (1, Account(1L, 500d, RUB)),
      (2, Account(2L, 100d, RUB)),
      (3, Account(3L, 200d, RUB))
    )
    val initRef: Task[Ref[Task, Map[Long, Account]]] = Ref.of(initMap)

    def getAccountService(store: Ref[Task, Map[Long, Account]]): Task[AccountService[Task]] =
      for {
        _ <- Task.unit
        inMemoryLogger = TaskLogger("InMemoryTest")
        accServiceLogger = TaskLogger("AccountServiceLogger")
        accAlg = InMemoryAccountAlg[Task](store, inMemoryLogger)
      } yield AccountService(accAlg, accServiceLogger)

  }

  "AccountsServiceSpec.getAccount" should "return account" in new mix {
    val id = 1
    runTask(for {
      store <- initRef
      service <- getAccountService(store)
      account <- service.getAccount(id)
    } yield {
      account shouldEqual Right(Account(1L, 500d, RUB))
    })
  }

  it should "return AccountNotFound exception if account is missing" in new mix {
    val id = -1
    runTask(for {
      store <- initRef
      service <- getAccountService(store)
      account <- service.getAccount(id)
    } yield {
      account shouldEqual Left(AccountNotFound)
    })
  }
}
