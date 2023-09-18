package com.github.atais.medicover.http

import com.github.atais.medicover.cache.{BlobDictStore, DictStore, MemoryDictStore}
import sttp.model.headers.CookieWithMeta

trait CookieStore extends DictStore {
  override type K = String
  override type V = Seq[CookieWithMeta]
}

private[http] class MemoryCookieStore extends MemoryDictStore with CookieStore

private[http] class PersistentCookieStore(override protected val filename: String)
    extends BlobDictStore
    with CookieStore
