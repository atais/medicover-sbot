package com.github.atais.cli.options

import com.github.atais.medicover.http.Session
import zio.{IO, ZIO}

case class Exit()(override implicit val session: Session) extends State {
  override def render: IO[Unit, String] =
    ZIO.succeed("Goodbye!")

  override def process(input: String, previous: State): IO[Unit, State] =
    ZIO.fail[Unit](())
}
