package com.github.atais.http

import sttp.model.headers.CookieWithMeta

import java.io._
import java.nio.file.{Files, Paths}
import java.util.concurrent.ConcurrentHashMap

object CookieStore {
  private val cacheDir = Paths.get(".cache")
  Files.createDirectories(cacheDir)

  private type Host    = String
  private type Cookies = ConcurrentHashMap[Host, Seq[CookieWithMeta]]
}

class CookieStore(filename: String) {

  import com.github.atais.http.CookieStore._

  private val file: File   = new File(cacheDir.toString, filename)
  private val mem: Cookies = loadFile()

  def get(in: Host): Seq[CookieWithMeta] =
    Option(mem.get(in)).getOrElse(Seq.empty)

  def put(host: String, withMeta: Seq[CookieWithMeta]): Unit = {
    mem.put(host, withMeta)
    syncFile()
  }

  private def syncFile(): Unit = {
    val stream = new FileOutputStream(file, false)
    val oos    = new ObjectOutputStream(stream)
    oos.writeObject(mem)
    oos.close()
    stream.close()
  }

  private def loadFile(): Cookies =
    if (file.exists()) {
      val ois   = new ObjectInputStream(new FileInputStream(file))
      val value = ois.readObject
      ois.close()
      value.asInstanceOf[Cookies]
    } else {
      new ConcurrentHashMap()
    }

}
