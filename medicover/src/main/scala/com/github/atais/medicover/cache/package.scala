package com.github.atais.medicover
import java.nio.file.{Files, Path, Paths}

package object cache {

  val cacheDir: Path = Paths.get(".cache")
  Files.createDirectories(cacheDir)

}
