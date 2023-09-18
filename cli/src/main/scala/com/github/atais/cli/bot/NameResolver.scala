package com.github.atais.cli.bot

import com.github.atais.medicover.cache.BlobDictStore
import zio.macros.accessible
import zio.{UIO, _}

import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap
import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala

@accessible
trait NameResolver {

  def getService(id: Long): UIO[String]
//  def getClinic(id: Long): UIO[String]
//  def getDoctor(id: Long): UIO[String]
//  def getLanguage(id: Long): UIO[String]

  class NameStore(override protected val filename: String) extends BlobDictStore {
    override type K = Long
    override type V = String

    private val separator: Char = ':'

    override protected def saveToFile(): Unit = {
      val stream = new FileOutputStream(file, false)
      mem.asScala.toSeq
        .sortBy(_._1)
        .map { case (k, v) => s"$k$separator$v" }
        .foreach(s => stream.write(s.getBytes))
      stream.close()
    }

    override protected def loadFromFile(): StoreMap = {
      val map: StoreMap = new ConcurrentHashMap()
      if (file.exists()) {
        val source = scala.io.Source.fromFile(file)
        source
          .getLines()
          .map { line =>
            val idx = line.indexOf(separator.toString)
            line.splitAt(idx)
          }
          .foreach { case (k, v) => map.put(k.toLong, v) }
        source.close()
      }
      map
    }
  }
}

trait NameResolverLive extends NameResolver {

  private val ext                 = "properties"
  private val services: NameStore = new NameStore(s"services.$ext")
//  private val clinics: NameStore   = new NameStore(s"clinics.$ext")
//  private val doctors: NameStore   = new NameStore(s"doctors.$ext")
//  private val languages: NameStore = new NameStore(s"languages.$ext")

  override def getService(id: Long): UIO[String] =
    ZIO.succeed {
      services.get(id) match {
        case Some(value) => value
        case None        => ???
      }
    }
}
