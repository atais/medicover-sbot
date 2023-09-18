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

  case class FilterParams(
    regionIds: Seq[Long],
    serviceTypeId: Long,
    serviceIds: Seq[Long],
    clinicIds: Seq[Long],
    selectedSpecialties: Seq[Long]
  )

  implicit def params[T <: Product](in: T): Seq[(String, String)] =
    in.productElementNames
      .zip(in.productIterator)
      .flatMap {
        case (key, value: Seq[_]) =>
          value.map(v => (key, v.toString))
        case (key, value) =>
          Seq((key, value.toString))
      }
      .toSeq

}
