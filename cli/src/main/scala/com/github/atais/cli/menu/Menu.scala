package com.github.atais.cli.menu

import enumeratum._

sealed trait Menu extends EnumEntry

object Menu extends Enum[Menu] {
  override def values: IndexedSeq[Menu] = findValues

  case object Login                             extends Menu
  case object Search                            extends Menu
  case object Quit                              extends Menu
  case class Invalid(input: NoSuchMember[Menu]) extends Menu

}
