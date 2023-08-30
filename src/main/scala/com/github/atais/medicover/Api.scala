package com.github.atais.medicover

import com.github.atais.http.Session
import com.github.atais.medicover.Static._
import sttp.client3.UriContext

object Api {

  def getRegions(implicit s: Session): String = {
    val res = s.send(
      mRequest.get(uri"$molUrl/api/MyVisits/SearchFreeSlotsToBook/GetInitialFiltersData")
    )
    res.body.toOption.get
  }

}
