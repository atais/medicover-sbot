package com.github.atais.cli.options

import cats.implicits.toShow
import com.github.atais.cli.bot.PrettyPrint._
import com.github.atais.medicover.request.VisitParams
import zio.{IO, ZIO}

import java.time.{LocalDateTime, LocalTime}

case class SearchInput(vp: VisitParams) extends State {
  override def render: String =
    s"""
       |Current state: ${vp.show}
       |Provide (r)egion, (s)ervice, (c)linic as (x):value or s(e)arch""".stripMargin

  override def process(input: String, previous: State): IO[Unit, State] = {
    val (option, value) = {
      if (input == "e") ("e", "0")
      else input.splitAt(input.indexOf(':'))
    }
    option match {
      case "r" => ZIO.succeed(SearchInput(vp.copy(regionIds = vp.regionIds :+ value.toLong)))
      case "s" => ZIO.succeed(SearchInput(vp.copy(serviceIds = vp.serviceIds :+ value.toLong)))
      case "c" => ZIO.succeed(SearchInput(vp.copy(clinicIds = vp.clinicIds :+ value.toLong)))
      case "e" => ZIO.succeed(Search(vp))
      case _   => ZIO.succeed(Menu)
    }
  }
}

object SearchInput {
  val empty: SearchInput = SearchInput(
    VisitParams(
      regionIds = Seq(204),
      serviceTypeId = 2,
      serviceIds = Seq(202, 64796),
      clinicIds = Seq(12396, 3154),
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
