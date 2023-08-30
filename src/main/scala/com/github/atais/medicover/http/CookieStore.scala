package com.github.atais.medicover.http

import com.github.atais.medicover.http.CookieStore._
import sttp.model.headers.CookieWithMeta

import java.io._
import java.nio.file.{Files, Path, Paths}
import java.util.concurrent.ConcurrentHashMap

object CookieStore {
  val cacheDir: Path = Paths.get(".cache")
  Files.createDirectories(cacheDir)

  type Host    = String
  type Cookies = ConcurrentHashMap[Host, Seq[CookieWithMeta]]
}

trait CookieStore {
  def get(in: Host): Seq[CookieWithMeta]
  def put(host: String, withMeta: Seq[CookieWithMeta]): Seq[CookieWithMeta]
}

private[http] class MemoryCookieStore extends CookieStore {
  protected val mem: Cookies = new ConcurrentHashMap()

  def get(in: Host): Seq[CookieWithMeta] =
    Option(mem.get(in)).getOrElse(Seq.empty)

  def put(host: String, withMeta: Seq[CookieWithMeta]): Seq[CookieWithMeta] =
    mem.put(host, withMeta)
}

private[http] class PersistentCookieStore(filename: String) extends MemoryCookieStore {

  private val file: File = new File(cacheDir.toString, filename)

  override protected val mem: Cookies = loadFromFile()

  override def put(host: String, withMeta: Seq[CookieWithMeta]): Seq[CookieWithMeta] = {
    val r = super.put(host, withMeta)
    saveToFile()
    r
  }

  private def saveToFile(): Unit = {
    val stream = new FileOutputStream(file, false)
    val oos    = new ObjectOutputStream(stream)
    oos.writeObject(mem)
    oos.close()
    stream.close()
  }

  private def loadFromFile(): Cookies =
    if (file.exists()) {
      val ois   = new ObjectInputStream(new FileInputStream(file))
      val value = ois.readObject
      ois.close()
      value.asInstanceOf[Cookies]
    } else {
      new ConcurrentHashMap()
    }

}
