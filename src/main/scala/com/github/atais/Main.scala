package com.github.atais

import com.github.atais.HttpCommon.*

import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import org.jsoup.select.NodeFilter
import org.slf4j.{Logger, LoggerFactory}
import sttp.client3.*
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import sttp.model.*

import java.net.URLDecoder

object Main extends App {

  val log = LoggerFactory.getLogger(getClass)

  Login.apply()

}
