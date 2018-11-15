package com.kvitral.utils

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.kvitral.transformers.EffectToRoute
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest.Suite

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}

trait TaskRouteTest extends Suite with ScalatestRouteTest {
  val monixScheduler: Scheduler = monix.execution.Scheduler.global

  override implicit val executor: ExecutionContextExecutor = new ExecutionContextExecutor() {
    override def reportFailure(cause: Throwable): Unit = monixScheduler.reportFailure(cause)

    override def execute(runnable: Runnable): Unit = monixScheduler.execute(runnable)
  }

  implicit val testTaskToRoute: EffectToRoute[Task] = EffectToRoute.convertTask(monixScheduler)

  def runTask[A](task: Task[A]): Unit = Await.result(task.runToFuture(monixScheduler), Duration.Inf)
}
