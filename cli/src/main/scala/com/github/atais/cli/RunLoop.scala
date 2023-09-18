package com.github.atais.cli

import com.github.atais.cli.options.{Exit, State}
import zio.Console.{printLine, readLine}
import zio.macros.accessible
import zio.{ULayer, ZIO, ZLayer}

@accessible
trait RunLoop {
  def step(state: State): ZIO[Any, Any, State]
}

object RunLoop {
  val live: ULayer[RunLoop] = ZLayer.succeed(new RunLoopLive {})
}

trait RunLoopLive extends RunLoop {
  override def step(state: State): ZIO[Any, Any, State] =
    for {
      _         <- printLine(state.render)
      input     <- if (state == Exit) ZIO.succeed("") else readLine
      nextState <- state.process(input, state)
    } yield nextState
}
