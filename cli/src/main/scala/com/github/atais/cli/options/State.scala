package com.github.atais.cli.options

import com.github.atais.medicover.http.Session
import zio.IO

trait State {
  implicit val session: Session
  def render: IO[Any, String]
  def process(input: String, previous: State): IO[Unit, State]
}
