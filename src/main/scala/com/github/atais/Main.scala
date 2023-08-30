package com.github.atais

import com.github.atais.http.Session
import com.github.atais.medicover.{Credentials, Login}
import org.slf4j.LoggerFactory
import sttp.client3._
import sttp.client3.logging.slf4j.Slf4jLoggingBackend

object Main extends App {

  val log = LoggerFactory.getLogger(getClass)

  val credentials = Credentials.fromProperties()

  private val backend: SttpBackend[Identity, Any] = Slf4jLoggingBackend(HttpClientSyncBackend())
  val client: SimpleHttpClient                    = SimpleHttpClient(backend)
  val session: Session                            = new Session(credentials)(client)

  val r = Login(credentials)(session)
  println(r)
}
