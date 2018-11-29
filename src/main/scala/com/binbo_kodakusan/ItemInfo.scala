package com.binbo_kodakusan

import java.io.File

/**
  * アイテム
  */
sealed trait ItemInfo {
  val Path: String
  val Name: String
}

/**
  * ファイルアイテム
  *
  * @param Path
  * @param Name
  */
case class FileInfo(val Path: String, val Name: String)
  extends ItemInfo

object FileInfo {
  def apply(f: File) = new FileInfo(f.getPath, f.getName)
}

/**
  * ディレクトリアイテム
  *
  * @param Path
  * @param Name
  */
case class DirectoryInfo(val Path: String, val Name: String)
  extends ItemInfo

object DirectoryInfo {
  def apply(f: File) = new DirectoryInfo(f.getPath, f.getName)
}
