package com.github.atais.http

import sttp.client3.{Request, Response, SimpleHttpClient}
import sttp.model.headers.{CookieValueWithMeta, CookieWithMeta}

import java.util.concurrent.ConcurrentHashMap

object Session {
  private type Host    = String
  private type Cookies = ConcurrentHashMap[Host, Seq[CookieWithMeta]]
}

class Session(client: SimpleHttpClient) {
  import com.github.atais.http.Session._

  private val cookieStore: Cookies = new ConcurrentHashMap()

  def withSession[T](request: Request[T, Any]): Response[T] = {
    val host           = request.uri.host.orNull
    val hostCookies    = Option(cookieStore.get(host)).getOrElse(Seq.empty)
    val withCookies    = request.cookies(hostCookies)
    val response       = client.send(withCookies)
    val newCookies     = response.unsafeCookies.filter(_.value.nonEmpty) // this fixes "set-cookie: idsrv=;" - wtf is that
    val updatedCookies = toMap(hostCookies) ++ toMap(newCookies)
    val withMeta       = updatedCookies.toSeq.map { case (n, c) => CookieWithMeta(n, c) }
    cookieStore.put(host, withMeta)
    response
  }

  private def toMap(in: Seq[CookieWithMeta]): Map[String, CookieValueWithMeta] =
    in.map { case CookieWithMeta(name, valueWithMeta) => (name, valueWithMeta) }.toMap

}
