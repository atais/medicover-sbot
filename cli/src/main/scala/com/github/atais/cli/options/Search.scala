package com.github.atais.cli.options
import cats.implicits.toShow
import com.github.atais.cli.bot.PrettyPrint._
import com.github.atais.cli.options.Search.safeRetry
import com.github.atais.medicover.http.Session
import com.github.atais.medicover.request.VisitParams
import com.github.atais.medicover.{Api, response}
import zio._

case class Search(vp: VisitParams)(override implicit val session: Session) extends State {

  private val query: IO[String, Seq[response.Visit]] =
    ZIO.fromEither(Api.getAvailableVisits(vp)).flatMap { r =>
      if (r.items.isEmpty) ZIO.fail("Did not find any items")
      else ZIO.succeed(r.items)
    }

  override def render: IO[Unit, String] =
    query
      .map(_.show)
      .mapError(e => println(e))
      .retry(safeRetry)

  override def process(input: String, previous: State): IO[Unit, State] =
    input.toLowerCase match {
      case _ => ZIO.succeed(Menu())
    }
}

object Search {
  val safeRetry = Schedule.spaced(10.minutes).jittered(0.75, 0.95)
}
