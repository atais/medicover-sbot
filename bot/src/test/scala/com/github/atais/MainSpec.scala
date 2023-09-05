package com.github.atais

import zio.Scope
import zio.test._

object MainSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Nothing] =
    suite("Main Spec")(
      test("prints to console") {
        for {
          _      <- Main.run
          output <- TestConsole.output
        } yield assertTrue(output == Vector.empty)
      }
    )
}
