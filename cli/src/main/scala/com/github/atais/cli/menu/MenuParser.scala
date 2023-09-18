package com.github.atais.cli.menu

import com.github.atais.cli.menu.Menu.Invalid
import zio._
import zio.macros.accessible

@accessible
trait MenuParser {
  def parse(input: String): UIO[Menu]
}

object MenuParser {
  val live: ULayer[MenuParser] = ZLayer.succeed(new MenuParserLive {})
}

trait MenuParserLive extends MenuParser {
  override def parse(input: String): UIO[Menu] =
    ZIO.succeed {
      Menu.withNameInsensitiveEither(input) match {
        case Left(nsm) => Invalid(nsm)
        case Right(v)  => v
      }
    }
}
