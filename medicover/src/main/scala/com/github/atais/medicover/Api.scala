package com.github.atais.medicover

import com.github.atais.medicover.http.Session
import com.github.atais.medicover.request._
import com.github.atais.medicover.response._
import com.github.plokhotnyuk.jsoniter_scala.core._
import sttp.client3.UriContext
import sttp.model.{HeaderNames, MediaType}

object Api {

  def getRegions(implicit s: Session): Either[String, Regions] = {
    val res = s.send(mRequest.get(uri"$molUrl/api/MyVisits/SearchFreeSlotsToBook/GetInitialFiltersData"))
    val obj = res.body.map(readFromString[Regions](_))
    obj
  }

  def getFiltersData(fp: FilterParams)(implicit s: Session): Either[String, DictData] = {
    val headers = Map(
      HeaderNames.Accept      -> MediaType.ApplicationJson.toString(),
      HeaderNames.ContentType -> MediaType.ApplicationJson.toString()
    )
    val res =
      s.send(
        mRequest.get(uri"$molUrl/api/MyVisits/SearchFreeSlotsToBook/GetFiltersData".addParams(fp: _*)).headers(headers)
      )
    val obj = res.body.map(readFromString[DictData](_))
    obj
  }

  def getAvailableVisits(vp: VisitParams)(implicit s: Session): Either[String, AvailableVisits] = {
    val json = writeToString(vp)
    val headers = Map(
      HeaderNames.Accept      -> MediaType.ApplicationJson.toString(),
      HeaderNames.ContentType -> MediaType.ApplicationJson.toString()
    )
    val res =
      s.send(mRequest.post(uri"$molUrl/api/MyVisits/SearchFreeSlotsToBook?language=pl-PL").headers(headers).body(json))
    val obj = res.body.map(readFromString[AvailableVisits](_))
    obj
  }
}
