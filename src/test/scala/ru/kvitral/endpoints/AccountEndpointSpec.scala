package ru.kvitral.endpoints

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.MessageEntity
import akka.http.scaladsl.testkit.ScalatestRouteTest
import cats.effect.concurrent.Ref
import com.kvitral.endpoints.AccountEndpoint
import com.kvitral.model.errors.AccountNotFound
import com.kvitral.model.{Account, ErrorMessage, Transaction}
import com.kvitral.repository._
import com.kvitral.services.AccountService
import monix.eval.Task
import org.scalatest.{FlatSpec, Matchers}
import ru.kvitral.utils.TaskRouteTest
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import org.scalatest.concurrent.ScalaFutures._


class AccountEndpointSpec extends FlatSpec with Matchers with ScalatestRouteTest with TaskRouteTest {

  trait mix {
    val initMap = Map[Long, Account]((1, Account(1L, 500d, "RUB")), (2, Account(2L, 100d, "RUB")))

    def getRoutes: Task[AccountEndpoint[Task]] = {
      for {
        initStore <- Ref.of[Task, Map[Long, Account]](initMap)
        inMemoryLogger = TaskLogger("InMemoryTest")
        accountServiceLogger = TaskLogger("AccountServiceLogger")
        inMemoryAccountAlg = InMemoryAccountAlg[Task](initStore, inMemoryLogger)
        accountService = AccountService[Task](inMemoryAccountAlg, accountServiceLogger)
        accountEndpoint = AccountEndpoint[Task](accountService)
      } yield accountEndpoint
    }
  }

  "AccountEndpoint.getAccounts" should "return existing account" in new mix {
    runTask(for {
      acc <- getRoutes
      routes <- acc.getAccountRoute

    } yield Get("/getAccounts?accountId=1") ~> routes ~> check {
      responseAs[Account] shouldEqual Account(1L, 500d, "RUB")
    })
  }

  it should "return error message if account is not found" in new mix {
    runTask(for {
      acc <- getRoutes
      routes <- acc.getAccountRoute
    } yield Get("/getAccounts?accountId=-1") ~> routes ~> check {
      responseAs[ErrorMessage] shouldEqual ErrorMessage(s"Couldn`t find account with id -1", AccountNotFound)
    })
  }

  "AccountEndpoint.transfer" should "change balances according to input" in new mix {
    val transaction = Transaction(1, 2, 100, "RUB")
    val transactionEntity = Marshal(transaction).to[MessageEntity].futureValue
    val request = Post("/transfer").withEntity(transactionEntity)
    runTask(for {
      acc <- getRoutes
      routes <- acc.getAccountRoute

    } yield request ~> routes ~> check {

      responseAs[String] shouldEqual "OK"
    })
  }
  it should "return error message if something is wrong" in new mix {
    val transaction = Transaction(-1, 1, 100, "RUB")
    val transactionEntity = Marshal(transaction).to[MessageEntity].futureValue
    val request = Post("/transfer").withEntity(transactionEntity)
    runTask(for {
      acc <- getRoutes
      routes <- acc.getAccountRoute

    } yield request ~> routes ~> check {

      responseAs[ErrorMessage] shouldEqual ErrorMessage("something went wrong", AccountNotFound)
    })
  }
}
