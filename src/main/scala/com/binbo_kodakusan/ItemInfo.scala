package com.binbo_kodakusan

import java.io.File

case class FullPath(value: String)
case class FileName(value: String)
/**
  * アイテム
  */
sealed trait ItemInfo {
  val Path: FullPath
  val Name: FileName
}

/**
  * ファイルアイテム
  *
  * @param Path
  * @param Name
  */
case class FileInfo(val Path: FullPath, val Name: FileName)
  extends ItemInfo

object FileInfo {
  def apply(f: File) =
    new FileInfo(FullPath(f.getPath), FileName(f.getName))
}

/**
  * ディレクトリアイテム
  *
  * @param Path
  * @param Name
  */
case class DirectoryInfo(val Path: FullPath, val Name: FileName)
  extends ItemInfo

object DirectoryInfo {
  def apply(f: File) =
    new DirectoryInfo(FullPath(f.getPath), FileName(f.getName))
}
