package com.github.atais.medicover

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros._

import java.time.{LocalDateTime, LocalTime}

object request {

  case class VisitParams(
    regionIds: Seq[Long],
    serviceTypeId: Long,
    serviceIds: Seq[Long],
    clinicIds: Seq[Long],
    doctorLanguagesIds: Seq[Long],
    doctorIds: Seq[Long],
    searchSince: LocalDateTime,
    startTime: LocalTime,
    endTime: LocalTime,
    selectedSpecialties: Seq[Long],
    visitType: String,
    isLastMinute: Boolean,
    disablePhoneSearch: Boolean,
    isChangeDate: Boolean
  )

  implicit val VisitParamsCodec: JsonValueCodec[VisitParams] = JsonCodecMaker.make

}
