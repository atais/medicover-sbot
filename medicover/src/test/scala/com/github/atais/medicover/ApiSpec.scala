package com.github.atais.medicover

import com.github.atais.medicover.ApiSpec.exampleVisitParams
import com.github.atais.medicover.request.VisitParams
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.{LocalDateTime, LocalTime}
class ApiSpec extends AnyFlatSpec with Matchers with MolClient {

  it should "get regions" in {
    Api.getRegions should not be Left
  }

  it should "get visits" in {
    Api.getAvailableVisits(exampleVisitParams) should not be Left
  }

}

object ApiSpec {
  val exampleVisitParams: VisitParams = VisitParams(
    regionIds = Seq(204),
    serviceTypeId = 2,
    serviceIds = Seq(176),
    clinicIds = Seq.empty,
    doctorLanguagesIds = Seq.empty,
    doctorIds = Seq.empty,
    searchSince = LocalDateTime.now(),
    startTime = LocalTime.MIDNIGHT,
    endTime = LocalTime.MIDNIGHT.minusMinutes(1),
    selectedSpecialties = Seq(176),
    visitType = "0",
    isLastMinute = false,
    disablePhoneSearch = false,
    isChangeDate = false
  )
}
