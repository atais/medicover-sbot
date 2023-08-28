package com.github.atais

import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import org.jsoup.select.NodeFilter
import org.slf4j.{Logger, LoggerFactory}
import sttp.client3.*
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import sttp.model.*

object HttpCommon {

  private val backend: SttpBackend[Identity, Any] = Slf4jLoggingBackend(HttpClientSyncBackend())
  val client: SimpleHttpClient                    = SimpleHttpClient(backend)

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
        HeaderNames.AcceptEncoding -> "gzip, deflate, br",
      ),
      true
    )

}
