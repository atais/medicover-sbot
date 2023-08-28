package com.github.atais

import HttpCommon.*

import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import org.jsoup.select.NodeFilter
import org.slf4j.{Logger, LoggerFactory}
import sttp.client3.*
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import sttp.model.*
import sttp.model.headers.{CookieValueWithMeta, CookieWithMeta}

import java.net.URLDecoder
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object Session {
  private type Host    = String
  private type Cookies = ConcurrentHashMap[Host, Seq[CookieWithMeta]]
}

class Session(client: SimpleHttpClient) {
  import Session.*

  private val cookieStore: Cookies = new ConcurrentHashMap()

  def withSession[T](request: Request[T, Any]): Response[T] = {
    val host           = request.uri.host.orNull
    val hostCookies    = Option(cookieStore.get(host)).getOrElse(Seq.empty)
    val withCookies    = request.cookies(hostCookies)
    val response       = client.send(withCookies)
    val updatedCookies = toMap(hostCookies) ++ toMap(response.unsafeCookies)
    val withMeta       = updatedCookies.toSeq.map((CookieWithMeta.apply _).tupled)
    cookieStore.put(host, withMeta)
    response
  }

  private def toMap(in: Seq[CookieWithMeta]): Map[String, CookieValueWithMeta] =
    in
      .filter(_.value.nonEmpty) // this fixes "set-cookie: idsrv=;" - wtf is that
      .map { case CookieWithMeta(name, valueWithMeta) => (name, valueWithMeta) }
      .toMap

}
