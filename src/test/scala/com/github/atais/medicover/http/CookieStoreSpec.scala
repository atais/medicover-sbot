package com.github.atais.medicover.http

import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.model.headers.{CookieValueWithMeta, CookieWithMeta}

import java.nio.file.{Files, Paths}

class CookieStoreSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  private val host = "host"
  private val name = "name"
  private val cookie1 =
    CookieValueWithMeta("value1", None, None, None, None, secure = true, httpOnly = true, None, Map.empty)
  private val cookie2  = cookie1.copy(value = "value2")
  private val fakename = "testcase"

  it should "save cookie" in {
    val cookieStore = new CookieStore(fakename)

    cookieStore.put(host, Seq(CookieWithMeta(name, cookie1)))
    cookieStore.get(host) shouldBe Seq(CookieWithMeta(name, cookie1))
    cookieStore.put(host, Seq(CookieWithMeta(name, cookie2)))
    cookieStore.get(host) shouldBe Seq(CookieWithMeta(name, cookie2))
  }

  it should "restore session from file" in {
    val cookieStore = new CookieStore(fakename)
    cookieStore.get(host) shouldBe Seq(CookieWithMeta(name, cookie2))
  }

  override protected def afterAll(): Unit = {
    Files.delete(Paths.get(".cache", fakename))
    super.afterAll()
  }
}
