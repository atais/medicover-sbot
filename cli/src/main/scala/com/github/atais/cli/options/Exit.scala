package com.github.atais.cli.options

import zio.{IO, ZIO}

case object Exit extends State {
  override def render: String =
    "Goodbye!"

  override def process(input: String, previous: State): IO[Unit, State] =
    ZIO.fail[Unit](())
}
