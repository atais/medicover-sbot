package com.github.atais.cli.options

import zio.IO

trait State {
  def render: String
  def process(input: String, previous: State): IO[Unit, State]
}
