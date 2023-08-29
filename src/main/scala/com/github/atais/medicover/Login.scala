package com.github.atais.medicover

import com.github.atais.http.Session
import com.github.atais.medicover.HttpCommon._
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import sttp.client3._
import sttp.model._

import java.util.Properties
import scala.jdk.CollectionConverters._

object Login {

  private val log = LoggerFactory.getLogger(getClass)
  private val req = mRequest
  private val credentials: Map[String, String] = {
    val p = new Properties()
    p.load(getClass.getResourceAsStream("/credentials.properties"))
    p.asScala.toMap
  }

  def apply(client: SimpleHttpClient): Unit = {
    val session = new Session(client)

    if (isLoggedIn(session)) {
      ()
    } else {
      log.info("No valid session")
      login(session)
    }
  }

  def isLoggedIn(ss: Session): Boolean = {
    log.info("Check if previous session is still valid")
    val res = ss.withSession(mRequest.get(molUrl))
    res.code == StatusCode.Ok
  }

  def login(ss: Session): Unit = {

    log.info("Step #1")
    val res1 = ss.withSession(req.get(uri"$molUrl/Users/Account/LogOn?ReturnUrl=/"))
    val red1 = res1.header(HeaderNames.Location).get
    log.info(red1)

    log.info("Step #2")
    val res2         = ss.withSession(req.get(uri"$red1"))
    val oAuthReferer = res2.header(HeaderNames.Location).get
    val signinId     = oAuthReferer.split('=').last

    log.info(oAuthReferer)
    log.info(signinId)

    log.info("Step #3")
    val _ = ss.withSession(req.get(uri"$oAuthReferer"))

    log.info("Step #4")
    val res4 = ss.withSession(
      req
        .get(
          uri"$oauthUrl/external".addParams(
            Map(
              "provider"   -> "IS3",
              "signin"     -> signinId,
              "owner"      -> "Mcov_Mol",
              "ui_locales" -> "pl-PL"
            )
          )
        )
    )
    val red4 = res4.header(HeaderNames.Location).get

    log.info("Step #5")
    val res5 = ss.withSession(
      req
        .get(uri"$red4")
    )
    val loginUrlWithParams = res5.header(HeaderNames.Location).get

    log.info("Step #5b")
    val res5b = ss.withSession(
      req
        .get(uri"$loginUrlWithParams")
    )

    val loginForm = body2Form(res5b.body.toOption.get, "ReturnUrl", "__RequestVerificationToken") ++ credentials
    log.info(loginForm.mkString("\n"))

    log.info("Step #6")
    val res6 = ss.withSession(
      req
        .post(uri"$loginUrlWithParams")
        .header(HeaderNames.Referer, oAuthReferer)
        .contentType("application/x-www-form-urlencoded")
        .body(loginForm)
    )
    log.info(res6.toString)

    val callback6    = res6.header(HeaderNames.Location).get
    val callback6Url = loginUrl.toString + callback6
    log.info(callback6Url)
    val res6b = ss.withSession(
      req
        .get(uri"$callback6Url")
        .header(HeaderNames.Referer, oAuthReferer)
    )

    val oauthForm = body2Form(res6b.body.toOption.get, "code", "id_token", "scope", "state", "session_state")
    log.info(oauthForm.mkString("\n"))

    val _ = ss.withSession(
      req
        .post(uri"$oauthUrl/signin-oidc")
        .header(HeaderNames.Referer, oAuthReferer)
        .body(oauthForm)
    )

    val res7b = ss.withSession(
      req
        .get(uri"$oauthUrl/callback")
        .header(HeaderNames.Referer, oAuthReferer)
    )
    log.info(res7b.toString)
    val callback7 = res7b.header(HeaderNames.Location).get

    log.info("Step #7c")
    val res7c = ss.withSession(
      req
        .get(uri"$callback7")
        .header(HeaderNames.Referer, oAuthReferer)
    )
    val oauthForm2 = body2Form(res7c.body.toOption.get, "code", "id_token", "scope", "state", "session_state")

    log.info("Step #8")
    val res8 = ss.withSession(
      req
        .post(uri"$molUrl/Medicover.OpenIdConnectAuthentication/Account/OAuthSignIn")
        .headers(
          Map(
            HeaderNames.Referer -> s"${oauthUrl.toString}/",
            "Origin"            -> s"${oauthUrl.toString}/",
            "Content-Type"      -> "application/x-www-form-urlencoded"
          )
        )
        .body(oauthForm2)
    )
    log.info(res8.toString)

    log.info("Step #9")
    val res9 = ss.withSession(
      req
        .get(molUrl)
        .headers(
          Map(
            HeaderNames.Referer -> s"$molUrl/Medicover.OpenIdConnectAuthentication/Account/OAuthSignIn"
          )
        )
    )

    val callback9    = res9.header(HeaderNames.Location).get
    val callback9Url = molUrl.toString + callback9
    log.info("Step #10")
    val res10 = ss.withSession(
      req
        .get(uri"$callback9Url")
    )
    log.info(res10.toString)

    log.info("Step #11")
    val res11 = ss.withSession(
      req
        .get(uri"https://mol.medicover.pl/api/MyVisits/SearchFreeSlotsToBook/GetInitialFiltersData")
    )
    log.info(res11.toString)
  }

  private def body2Form(body: String, params: String*): Map[String, String] = {
    val document = Jsoup.parse(body)
    params.map { param =>
      val value = document.select(s"input[name=$param]").first().attr("value")
      param -> value
    }.toMap
  }

}
