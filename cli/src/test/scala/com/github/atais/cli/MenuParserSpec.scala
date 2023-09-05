package com.github.atais.cli

import com.github.atais.cli.MenuParserSpecUtils.{invalidCommandsGen, parse}
import zio.{Scope, ZIO}
import zio.test._

object MenuParserSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("MenuParser")(
      suite("parse")(
        test("`new game` returns NewGame command") {
          parse("login").map(m => assertTrue(m == Menu.Login))
        },
        test("`resume` returns Resume command") {
          parse("search").map(m => assertTrue(m == Menu.Search))
        },
        test("`quit` returns Quit command") {
          parse("quit").map(m => assertTrue(m == Menu.Quit))
        },
        test("any other input returns Invalid command") {
          checkAll(invalidCommandsGen) { input =>
            parse(input).map(m => assertTrue(m.isInstanceOf[Menu.Invalid]))
          }
        }
      )
    )

}

object MenuParserSpecUtils {

  val validCommands: Seq[String] = Menu.values.map(_.entryName)

  val invalidCommandsGen: Gen[Any, String] =
    Gen.string.filter(str => !validCommands.contains(str))

  def parse(input: String): ZIO[Any, Nothing, Menu] = {
    val app    = MenuParser.parse(input)
    val result = app.provide(MenuParser.live)
    result
  }
}
