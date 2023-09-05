package com.github.atais.scheduler

import zio._

object Dispatcher extends ZIOAppDefault {

  override def run: ZIO[Any, Serializable, Int] = {

    val gamble: ZIO[Any, Nothing, Int] =
      Random.nextIntBetween(1, 11).map { i =>
        println(s"I have drawn $i")
        i
      }

    val check: ZIO[Any, String, Int] =
      gamble.flatMap { i =>
        if (i == 10) {
          println("you won, exitting")
          ZIO.succeed(i)
        } else {
          println(s" $i -- keep playing")
          ZIO.fail("try again")
        }
      }
    val s = Schedule.spaced(1.second).jittered(0.75, 0.95)

    val r: ZIO[Any, Serializable, Int] = for {
      _ <- Console.printLine("Hello, World!")
      _ <- Console.printLine("Lets gamble!")
      i <- check.retry(s)
    } yield {
      i
    }
    r
  }
}
