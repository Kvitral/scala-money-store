package ru.kvitral.endpoints

import akka.http.scaladsl.testkit.ScalatestRouteTest
import cats.effect.concurrent.Ref
import com.kvitral.endpoints.AccountEndpoint
import com.kvitral.model.{Account, ErrorMessage}
import com.kvitral.repository._
import com.kvitral.services.AccountService
import org.scalatest.{FlatSpec, Matchers}
import com.kvitral.model.errors.AccountNotFound
import com.kvitral.transformers.EffectToRoute._
import monix.eval.Task
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

class AccountEndpointSpec extends FlatSpec with Matchers with ScalatestRouteTest {

//  trait mix {
//
//    val initMap = Map[Long, Account]((1, Account(1L, 500d, "RUB")), (2, Account(2L, 100d, "RUB")))
//
//    def getRoutes: Task[AccountEndpoint[Task]] = {
//      for {
//        initStore <- Ref.of[Task, Map[Long, Account]](initMap)
//        inMemoryLogger = TaskLogger("InMemoryTest")
//        accountServiceLogger = TaskLogger("AccountServiceLogger")
//        inMemoryAccountAlg = InMemoryAccountAlg[Task](initStore, inMemoryLogger)
//        accountService = AccountService[Task](inMemoryAccountAlg, accountServiceLogger)
//        accountEndpoint = AccountEndpoint[Task](accountService)
//      } yield accountEndpoint
//    }
//
//    //    for {
//    //      idLogger = IdLogger("test")
//    //                   store <- Ref.of[Id](initMap)
//    //    }
//
//    //    val idLogger = new IdLogger("test")
//    //    val inMemoryAccountAlg = new InMemoryAccountAlg[Id](
//    //      Map[Long, Account]((1, Account(1L, 500d, "RUB")), (2, Account(2L, 100d, "RUB"))),
//    //      idLogger)
//    //    val accountService = new AccountService[Id](inMemoryAccountAlg, idLogger)
//    //    val accountEndpoint = new AccountEndpoint(accountService)
//  }
//
//  "AccountEndpoint" should "return existing account" in new mix {
//
//    for {
//      acc <- getRoutes
//      routes <- acc.getAccountRoute
//
//    } yield Get("/getAccounts?accountId=1") ~> routes ~> check {
//      responseAs[Account] shouldEqual Account(1L, 500d, "RUB")
//    }
//  }
//  it should "return error message if account is not found" in new mix {
//    for {
//      acc <- getRoutes
//      routes <- acc.getAccountRoute
//    } yield Get("/getAccounts?accountId=-1") ~> routes ~> check {
//      responseAs[ErrorMessage] shouldEqual ErrorMessage(s"Couldn`t find account with id -1", AccountNotFound)
//    }
//  }
}
