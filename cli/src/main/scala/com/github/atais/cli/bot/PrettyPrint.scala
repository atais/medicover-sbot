package com.github.atais.cli.bot

import cats.Show
import cats.implicits.toShow
import com.github.atais.medicover.response.{AvailableVisits, Visit}

object PrettyPrint {

  implicit val VisitShow: Show[Visit] = v =>
    s"""appointmentDate: ${v.appointmentDate}, doctorName: ${v.doctorName}, clinicName: ${v.clinicName}"""

  implicit val AvailableVisitsShow: Show[AvailableVisits] = v => s"""${v.items.map(_.show).mkString("\n")}"""

}
