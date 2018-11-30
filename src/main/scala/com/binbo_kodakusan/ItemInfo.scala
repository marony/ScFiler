package com.binbo_kodakusan

import java.io.File

case class Mark(value: Boolean)
case class FullPath(value: String)
case class FileName(value: String)

/**
  * アイテム
  */
sealed trait ItemInfo {
  val Path: FullPath
  val Name: FileName
  val Mark: Mark
}

/**
  * ファイルアイテム
  *
  * @param Path
  * @param Name
  */
case class FileInfo(val Path: FullPath, val Name: FileName, val Mark: Mark)
  extends ItemInfo

object FileInfo {
  def apply(f: File) =
    new FileInfo(FullPath(f.getPath), FileName(f.getName), Mark(false))
}

/**
  * ディレクトリアイテム
  *
  * @param Path
  * @param Name
  */
case class DirectoryInfo(val Path: FullPath, val Name: FileName, val Mark: Mark)
  extends ItemInfo

object DirectoryInfo {
  def apply(f: File) =
    new DirectoryInfo(FullPath(f.getPath), FileName(f.getName), Mark(false))
}
