package com.github.atais.cli.options

import com.github.atais.medicover.http.Session
import zio.{IO, ZIO}

case class Menu()(override implicit val session: Session) extends State {
  override def render: IO[Any, String] = ZIO.succeed(
    s"Choose your option: (l)ogin, (s)earch, (e)xit"
  )

  override def process(input: String, previous: State): IO[Unit, State] =
    input.toLowerCase match {
      case "l" => ZIO.succeed(???)
      case "s" => ZIO.succeed(SearchInput.empty)
      case "e" => ZIO.succeed(Exit())
      case _   => ZIO.succeed(Menu())
    }
}
