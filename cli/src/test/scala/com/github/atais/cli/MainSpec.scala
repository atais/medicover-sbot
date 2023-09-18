package com.github.atais.cli

import zio.test._

object MainSpec extends ZIOSpecDefault {

  override def spec =
    suite("Main Spec")(
      test("prints to console") {
        for {
          _      <- Main.run
          output <- TestConsole.output
        } yield assertTrue(output.nonEmpty)
      }
    )
}
