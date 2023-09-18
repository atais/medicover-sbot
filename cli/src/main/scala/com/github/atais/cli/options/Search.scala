package com.github.atais.cli.options

import com.github.atais.medicover.request.VisitParams
import zio.{IO, ZIO}

case class Search(vp: VisitParams) extends State {
  override def render: String =
    """I will search here"""

  override def process(input: String, previous: State): IO[Unit, State] =
    input.toLowerCase match {
      case _ => ZIO.succeed(Menu)
    }
}
