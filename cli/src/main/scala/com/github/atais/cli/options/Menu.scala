package com.github.atais.cli.options

import zio.{IO, ZIO}

case object Menu extends State {
  override def render: String =
    s"Choose your option: (l)ogin, (s)earch, (e)xit"

  override def process(input: String, previous: State): IO[Unit, State] =
    input.toLowerCase match {
      case "l" => ZIO.succeed(???)
      case "s" => ZIO.succeed(SearchInput.empty)
      case "e" => ZIO.succeed(Exit)
      case _   => ZIO.succeed(Menu)
    }
}
