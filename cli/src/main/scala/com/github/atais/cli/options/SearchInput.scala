package com.github.atais.cli.options

import cats.implicits.toShow
import com.github.atais.cli.bot.PrettyPrint._
import com.github.atais.medicover.http.Session
import com.github.atais.medicover.request.VisitParams
import zio.{IO, ZIO}

import java.time.{LocalDateTime, LocalTime}

case class SearchInput(vp: VisitParams)(override implicit val session: Session) extends State {
  override def render: IO[Any, String] = ZIO.succeed(
    s"""
       |Current state: ${vp.show}
       |Provide (r)egion, (s)ervice, (c)linic as (x):value or s(e)arch""".stripMargin
  )

  override def process(input: String, previous: State): IO[Unit, State] = {
    val parsed: IO[Serializable, (String, Long)] = input.split(':').toList match {
      case option :: value :: Nil => ZIO.attempt((option, value.toLong))
      case option :: _            => ZIO.succeed((option, 0L))
      case _                      => ZIO.fail(s"Invalid input $input")
    }

    parsed.mapBoth(
      _ => (),
      { case (option, value) =>
        option match {
          case "r" => SearchInput(vp.copy(regionIds = vp.regionIds :+ value))
          case "s" => SearchInput(vp.copy(serviceIds = vp.serviceIds :+ value))
          case "c" => SearchInput(vp.copy(clinicIds = vp.clinicIds :+ value))
          case "e" => Search(vp)
          case _   => Menu()
        }
      }
    )
  }
}

object SearchInput {
  def empty(implicit session: Session): SearchInput = SearchInput(
    VisitParams(
      regionIds = Seq(204),
      serviceTypeId = 2,
      serviceIds = Seq(202, 64796),
//      clinicIds = Seq(12396, 3154),
      clinicIds = Seq(12292),
      doctorLanguagesIds = Seq.empty,
      doctorIds = Seq.empty,
      searchSince = LocalDateTime.now(),
      startTime = LocalTime.MIDNIGHT,
      endTime = LocalTime.MIDNIGHT.minusMinutes(1),
      selectedSpecialties = Seq(163, 202, 64796),
      visitType = "0",
      isLastMinute = false,
      disablePhoneSearch = false,
      isChangeDate = false
    )
  )
}
