package com.binbo_kodakusan

object MarkHolder {
  type Marks = Seq[ItemInfo]

  def add(files: Marks, path: FullPath): Unit = {
    ???
  }

  def remove(marks: Marks, path: FullPath): Unit = {
    ???
  }

  def indexOf(marks: Marks, path: FullPath): Option[Int] = {
    ???
  }
}
