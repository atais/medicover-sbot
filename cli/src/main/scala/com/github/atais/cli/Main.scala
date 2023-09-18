package com.github.atais.cli

import cats.implicits.toShow
import com.github.atais.cli.bot.PrettyPrint._
import com.github.atais.cli.options._
import com.github.atais.medicover.http.Session
import com.github.atais.medicover.request._
import com.github.atais.medicover.{Api, Credentials}
import sttp.client3._
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import zio.Console.printLine
import zio.{ZIO, ZIOAppDefault}

import java.time.{LocalDateTime, LocalTime}

object Main extends ZIOAppDefault {

  private val backend: SttpBackend[Identity, Any] = Slf4jLoggingBackend(HttpClientSyncBackend())
  private val client: SimpleHttpClient            = SimpleHttpClient(backend)
  protected val credentials: Credentials          = Credentials.fromProperties()
  implicit val session: Session                   = new Session(credentials, client)

  val sampleVisit = VisitParams(
    regionIds = Seq(204),
    serviceTypeId = 2,
    serviceIds = Seq(202, 64796),
    clinicIds = Seq(12396, 3154),
    doctorLanguagesIds = Seq.empty,
    doctorIds = Seq.empty,
    searchSince = LocalDateTime.now(),
    startTime = LocalTime.MIDNIGHT,
    endTime = LocalTime.MIDNIGHT.minusMinutes(1),
    selectedSpecialties = Seq(163, 202, 64796),
    visitType = "0",
    isLastMinute = false,
    disablePhoneSearch = false,
    isChangeDate = false
  )

  val fp = FilterParams(
    regionIds = Seq(204),
    serviceTypeId = 2,
    serviceIds = Seq(163, 202, 64796),
    clinicIds = Seq(12396, 3154),
    selectedSpecialties = Seq(163, 202, 64796)
  )
  val cliApp =
    for {
      _ <- printLine("welcome")
      r  = Api.getAvailableVisits(sampleVisit)
      r <- ZIO.fromEither(r)
      _ <- printLine(r.show)
//      r2  = Api.getFiltersData(fp)
//      r2 <- ZIO.fromEither(r2)
//      _  <- printLine(r2)
    } yield r

  val program = {
    def loop(state: State): ZIO[RunLoop, Nothing, Unit] =
      RunLoop
        .step(state)
        .foldZIO(
          _ => ZIO.unit,
          nextState => loop(nextState)
        )
    loop(Menu())
  }

  override def run =
    program
      .provideLayer(RunLoop.live)
}
