package com.github.atais.cli.bot

import cats.Show
import cats.derived.semiauto
import cats.implicits.toShow
import com.github.atais.medicover.request.VisitParams
import com.github.atais.medicover.response.{AvailableVisits, Visit}

import java.time.{LocalDateTime, LocalTime}

object PrettyPrint {

  implicit val VisitShow: Show[Visit] = v =>
    s"""appointmentDate: ${v.appointmentDate}, doctorName: ${v.doctorName}, clinicName: ${v.clinicName}"""

  implicit val AvailableVisitsShow: Show[AvailableVisits] = v => s"""${v.items.map(_.show).mkString("\n")}"""

  implicit val LocalDateTimeShow: Show[LocalDateTime] = _.toString
  implicit val LocalTimeShow: Show[LocalTime]         = _.toString
  implicit val VisitParamsShow: Show[VisitParams]     = semiauto.showPretty

}
