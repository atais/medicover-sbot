package com.github.atais.medicover

import com.github.atais.medicover.http.Session
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import sttp.client3._
import sttp.model._

object Login {

  private val log = LoggerFactory.getLogger(getClass)
  private val req = mRequest

  def apply(credentials: Credentials)(implicit session: Session): Boolean =
    if (isLoggedIn) {
      true
    } else {
      log.info("No valid session")
      login(credentials)
      true
    }

  private def isLoggedIn(implicit s: Session): Boolean = {
    log.info("Check if previous session is still valid")
    val res = s.send(mRequest.get(molUrl))
    res.code == StatusCode.Ok
  }

  private def login(credentials: Credentials)(implicit s: Session): Unit = {
    log.info("Step #1")
    val res1 = s.send(req.get(uri"$molUrl/Users/Account/LogOn?ReturnUrl=/"))
    val red1 = res1.header(HeaderNames.Location).get
    log.info(red1)

    log.info("Step #2")
    val res2         = s.send(req.get(uri"$red1"))
    val oAuthReferer = res2.header(HeaderNames.Location).get
    val signinId     = oAuthReferer.split('=').last

    log.info(oAuthReferer)
    log.info(signinId)

    log.info("Step #3")
    val _ = s.send(req.get(uri"$oAuthReferer"))

    log.info("Step #4")
    val res4 = s.send(
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
    val res5 = s.send(
      req
        .get(uri"$red4")
    )
    val loginUrlWithParams = res5.header(HeaderNames.Location).get

    log.info("Step #5b")
    val res5b = s.send(
      req
        .get(uri"$loginUrlWithParams")
    )

    val loginForm = body2Form(res5b.body.toOption.get, "ReturnUrl", "__RequestVerificationToken") ++ credentials.asMap

    log.info("Step #6")
    val res6 = s.send(
      req
        .post(uri"$loginUrlWithParams")
        .header(HeaderNames.Referer, oAuthReferer)
        .contentType("application/x-www-form-urlencoded")
        .body(loginForm)
    )

    val callback6    = res6.header(HeaderNames.Location).get
    val callback6Url = loginUrl.toString + callback6
    log.info(callback6Url)
    val res6b = s.send(
      req
        .get(uri"$callback6Url")
        .header(HeaderNames.Referer, oAuthReferer)
    )

    val oauthForm = body2Form(res6b.body.toOption.get, "code", "id_token", "scope", "state", "session_state")

    val _ = s.send(
      req
        .post(uri"$oauthUrl/signin-oidc")
        .header(HeaderNames.Referer, oAuthReferer)
        .body(oauthForm)
    )

    val res7b = s.send(
      req
        .get(uri"$oauthUrl/callback")
        .header(HeaderNames.Referer, oAuthReferer)
    )
    log.info(res7b.toString)
    val callback7 = res7b.header(HeaderNames.Location).get

    log.info("Step #7c")
    val res7c = s.send(
      req
        .get(uri"$callback7")
        .header(HeaderNames.Referer, oAuthReferer)
    )
    val oauthForm2 = body2Form(res7c.body.toOption.get, "code", "id_token", "scope", "state", "session_state")

    log.info("Step #8")
    val _ = s.send(
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

    log.info("Step #9")
    val res9 = s.send(
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
    val _ = s.send(
      req
        .get(uri"$callback9Url")
    )
  }

  private def body2Form(body: String, params: String*): Map[String, String] = {
    val document = Jsoup.parse(body)
    params.map { param =>
      val value = document.select(s"input[name=$param]").first().attr("value")
      param -> value
    }.toMap
  }

}
