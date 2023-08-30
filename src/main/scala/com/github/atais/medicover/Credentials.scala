package com.github.atais.medicover

case class Credentials(user: String, pass: String) {
  val asMap: Map[String, String] = Map(
    "UserName" -> user,
    "Password" -> pass
  )
}

object Credentials {

  import pureconfig._
  import pureconfig.generic.auto._

  def fromProperties(file: String = "credentials.conf"): Credentials =
    ConfigSource.resources(file).loadOrThrow[Credentials]

}
