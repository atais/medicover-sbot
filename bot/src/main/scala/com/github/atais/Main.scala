package com.github.atais

import com.github.atais.cli.MenuParser
import zio.{ZIO, ZIOAppDefault}

object Main extends ZIOAppDefault {

//  private val log: Logger                         = LoggerFactory.getLogger(getClass)
//  private val backend: SttpBackend[Identity, Any] = Slf4jLoggingBackend(HttpClientSyncBackend())
//  private val client: SimpleHttpClient            = SimpleHttpClient(backend)

  val cliApp: ZIO[MenuParser, Nothing, Unit] =
    for {
      _ <- MenuParser.parse("key")
    } yield ()

  override def run: ZIO[Any, Nothing, Unit] =
    cliApp
      .provideLayer(MenuParser.live)
}
