package com.github.atais.medicover

import com.github.atais.medicover.http.Session
import sttp.client3._
import sttp.client3.logging.slf4j.Slf4jLoggingBackend

trait MolClient {

  private val backend: SttpBackend[Identity, Any] = Slf4jLoggingBackend(HttpClientSyncBackend())
  private val client: SimpleHttpClient            = SimpleHttpClient(backend)
  protected val credentials: Credentials          = Credentials.fromProperties()
  implicit val session: Session                   = new Session(credentials, client)

}
