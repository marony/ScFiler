package com.binbo_kodakusan

import java.io.File

/**
  * ファイル・ディレクトリ関連ユーティリティ
  */
object FileUtils {
  /**
    * ユーザのホームディレクトリを取得する
    *
    * @return ホームディレクトリ
    */
  def getHomeDirectory(): String = System.getProperty("user.home")

  /**
    * ディレクトリ内のファイル一覧(直下のみ)を返す
    *
    * @param path 検索するディレクトリパス
    * @return ファイル・ディレクトリ一覧
    */
  def getDirectoryContents(path: String): Seq[ItemInfo] = {
    if (path == null)
      return IndexedSeq.empty[ItemInfo]

    val p = new File(path)
    val files = p.listFiles()
    val r: IndexedSeq[ItemInfo] = {
      if (files != null)
        files.map(f => (if (f.isDirectory) DirectoryInfo(f) else FileInfo(f))).toIndexedSeq
      else
        IndexedSeq.empty[ItemInfo]
    }

    if (p.getParent != null)
      DirectoryInfo(path + File.separator + "..", "..") +: r
    else
      r
  }
}
