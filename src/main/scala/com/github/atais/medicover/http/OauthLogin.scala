package com.github.atais.medicover.http

import com.github.atais.medicover._
import org.jsoup.Jsoup
import sttp.client3._
import sttp.model._

/**
 * Heavily inspired on
 * https://github.com/apqlzm/medihunter/blob/master/medicover_session.py
 */
object OauthLogin {

  private val req = mRequest

  private[http] def apply(credentials: Credentials)(implicit s: Session): Unit = {
    // get authentication form
    val res1        = s.send(req.get(uri"$molUrl/Users/Account/LogOn?ReturnUrl=/"))
    val res2        = follow(follow(res1))
    val doc         = Jsoup.parse(res2.body.toOption.get)
    val metaContent = doc.select("meta[http-equiv=refresh]").attr("content")
    val metaUrl     = metaContent.replace("0; url = ", "")
    val res4        = s.send(req.get(uri"$metaUrl"))
    val res5        = follow(res4)

    // fill authentication
    val loginUrlWithParams = res5.header(HeaderNames.Location).get
    val loginForm          = body2Form(follow(res5), "ReturnUrl", "__RequestVerificationToken") ++ credentials.asMap
    val res6               = s.send(req.post(uri"$loginUrlWithParams").body(loginForm))

    // oauth form
    val oauthForm = body2Form(follow(res6), "code", "id_token", "scope", "state", "session_state")
    val _         = s.send(req.post(uri"$oauthUrl/signin-oidc").body(oauthForm))
    val res7b     = s.send(req.get(uri"$oauthUrl/callback"))

    // openid form
    val openIdForm = body2Form(follow(res7b), "code", "id_token", "scope", "state", "session_state")
    val openIdUrl  = "Medicover.OpenIdConnectAuthentication/Account/OAuthSignIn"
    val _          = s.send(req.post(uri"$molUrl/$openIdUrl").body(openIdForm))

    // enter and save cookies
    val res9 = s.send(req.get(molUrl))
    val _    = follow(res9)
  }

  private def follow[I](in: Response[I])(implicit s: Session): Response[Either[String, String]] = {
    val location = uri"${in.header(HeaderNames.Location).get}"
    val uri = if (location.host.isDefined) {
      location
    } else {
      Uri(
        scheme = in.request.uri.scheme,
        authority = in.request.uri.authority,
        pathSegments = location.pathSegments,
        querySegments = location.querySegments,
        fragmentSegment = location.fragmentSegment
      )
    }
    s.send(mRequest.get(uri))
  }

  private def body2Form(r: Response[Either[String, String]], params: String*): Map[String, String] = {
    val body     = r.body.toOption.get
    val document = Jsoup.parse(body)
    params.map { param =>
      val value = document.select(s"input[name=$param]").first().attr("value")
      param -> value
    }.toMap
  }

}
