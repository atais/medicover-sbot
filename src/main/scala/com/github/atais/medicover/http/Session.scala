package com.github.atais.medicover.http

import com.github.atais.medicover.{Credentials, molUrl}
import sttp.client3.{Request, Response, SimpleHttpClient}
import sttp.model.StatusCode
import sttp.model.headers.{CookieValueWithMeta, CookieWithMeta}

class Session(credentials: Credentials, client: SimpleHttpClient, cookieStore: CookieStore) {

  def this(credentials: Credentials, client: SimpleHttpClient) =
    this(credentials, client, new PersistentCookieStore(credentials.user))

  def send[T](request: Request[T, Any]): Response[T] = {
    val host        = request.uri.host.orNull
    val hostCookies = Option(cookieStore.get(host)).getOrElse(Seq.empty)
    val withCookies = request.cookies(hostCookies)
    val response    = client.send(withCookies)

    if (response.code == StatusCode.Unauthorized) {
      val inMemoryCookieStore = new MemoryCookieStore
      val inMemorySession     = new Session(credentials, client, inMemoryCookieStore)
      OauthLogin(credentials, inMemorySession)
      val sessionCookies = inMemoryCookieStore.get(molUrl.host.orNull)
      cookieStore.put(molUrl.host.orNull, sessionCookies): Unit
      send(request)
    } else {
      val newCookies     = response.unsafeCookies.filter(_.value.nonEmpty) // this fixes "set-cookie: idsrv=;" - wtf is that
      val updatedCookies = toMap(hostCookies) ++ toMap(newCookies)
      val withMeta       = updatedCookies.toSeq.map { case (n, c) => CookieWithMeta(n, c) }
      cookieStore.put(host, withMeta): Unit
      response
    }
  }

  private def toMap(in: Seq[CookieWithMeta]): Map[String, CookieValueWithMeta] =
    in.map { case CookieWithMeta(name, valueWithMeta) => (name, valueWithMeta) }.toMap

}
