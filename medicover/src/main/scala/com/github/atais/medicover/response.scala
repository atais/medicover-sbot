package com.github.atais.medicover

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker

import java.time.LocalDateTime

object response {

  case class Regions(
    regions: Seq[TextId],
    serviceTypes: Seq[TextId],
    homeLocationId: Long
  )

  case class TextId(text: String, id: Long)

  case class AvailableVisits(
    items: Seq[Visit],
    status: Long
  )

  case class Visit(
    id: String,
    appointmentDate: LocalDateTime,
    serviceId: Long,
    specializationName: String,
    doctorName: String,
    doctorId: Long,
    doctorScheduleId: Long,
    specialtyId: Long,
    sysVisitTypeId: Long,
    vendorTypeId: String,
    doctorLanguages: Seq[String],
    clinicId: Long,
    clinicName: String,
    isPhoneConsultation: Boolean,
    isBloodCollectionPointConsultation: Boolean,
    adHocYN: Boolean
  )

  implicit val RegionsCodec: JsonValueCodec[Regions]                 = JsonCodecMaker.make
  implicit val AvailableVisitsCodec: JsonValueCodec[AvailableVisits] = JsonCodecMaker.make

}
