package com.github.atais

import sttp.client3._
import sttp.model._

package object medicover {

  private val domain: String = "medicover.pl"
  val molUrl: Uri            = uri"https://mol.$domain"
  val molHost: String        = molUrl.host.orNull
  val oauthUrl: Uri          = uri"https://oauth.$domain"

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
