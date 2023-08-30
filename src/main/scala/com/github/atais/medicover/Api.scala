package com.github.atais.medicover

import com.github.atais.medicover.http.Session
import sttp.client3.UriContext

object Api {

  def getRegions(implicit s: Session): String = {
    val res = s.send(
      mRequest.get(uri"$molUrl/api/MyVisits/SearchFreeSlotsToBook/GetInitialFiltersData")
    )
    res.body.toOption.get
  }

}
