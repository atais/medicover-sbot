package com.github.atais.medicover

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ApiSpec extends AnyFlatSpec with Matchers with MolClient {


  it should "auth 1st" in {
    Login(credentials)(session)
  }

  it should "get regions" in {
    Api.getRegions should not be empty
  }

}
