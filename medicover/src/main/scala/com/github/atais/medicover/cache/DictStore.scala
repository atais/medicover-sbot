package com.github.atais.medicover.cache

import java.io._
import java.util.concurrent.ConcurrentHashMap

trait DictStore {
  type K
  type V
  type StoreMap = ConcurrentHashMap[K, V]

  def get(id: K): Option[V]
  def put(id: K, v: V): V
}

trait MemoryDictStore extends DictStore {
  protected val mem: StoreMap = new ConcurrentHashMap()

  override def get(id: K): Option[V] =
    Option(mem.get(id))

  override def put(id: K, v: V): V =
    mem.put(id, v)
}

trait BlobDictStore extends MemoryDictStore {
  protected val filename: String
  protected val file: File = new File(cacheDir.toString, filename)

  override protected val mem: StoreMap = loadFromFile()

  override def put(id: K, v: V): V = {
    val r = super.put(id, v)
    saveToFile()
    r
  }

  protected def saveToFile(): Unit = {
    val stream = new FileOutputStream(file, false)
    val oos    = new ObjectOutputStream(stream)
    oos.writeObject(mem)
    oos.close()
    stream.close()
  }

  protected def loadFromFile(): StoreMap =
    if (file.exists()) {
      val ois   = new ObjectInputStream(new FileInputStream(file))
      val value = ois.readObject
      ois.close()
      value.asInstanceOf[StoreMap]
    } else {
      new ConcurrentHashMap()
    }

}
