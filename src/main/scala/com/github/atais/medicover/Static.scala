package com.github.atais.medicover

import sttp.client3._
import sttp.model._

object Static {

  val domain   = "medicover.pl"
  val molUrl   = uri"https://mol.$domain"
  val loginUrl = uri"https://login.$domain"
  val oauthUrl = uri"https://oauth.$domain"

  val mRequest: RequestT[Empty, Either[String, String], Any] = basicRequest
    .followRedirects(false)
    .headers(
      Map(
        HeaderNames.UserAgent      -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36",
        HeaderNames.Accept         -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        HeaderNames.AcceptLanguage -> "pl,en-US,en;q=0.5",
        HeaderNames.AcceptEncoding -> "gzip, deflate, br"
      ),
      replaceExisting = true
    )

}
