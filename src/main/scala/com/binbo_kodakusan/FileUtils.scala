package com.binbo_kodakusan

import java.io.File
import java.nio.file.{ Files, Paths }
/**
  * ファイル・ディレクトリ関連ユーティリティ
  */
object FileUtils {
  val ParentPath = ".."

  /**
    * ファイルパスを結合する
    *
    * @param path
    * @return
    */
  def combine(path: String*): String = {
    val sb = new StringBuilder
    path.foreach(p => {
      var sep = ""
      if ((sb.length <= 0 || sb(sb.length - 1) != File.separatorChar) &&
        (p.length <= 0 || p(0) != File.separatorChar)) {
        sep = File.separator
      }
      if (sb.length > 0)
        sb ++= sep
      sb ++= p
    })
    sb.toString
  }

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
      DirectoryInfo(
        FullPath(FileUtils.combine(path, FileUtils.ParentPath)),
        FileName(FileUtils.ParentPath),
        Mark(false)) +: r
    else
      r
  }

  /**
    * ファイル/ディレクトリを削除する
    *
    * @param item
    */
  def deleteFile(item: ItemInfo): Unit = {
    def deleteFile_(path: String): Unit = {
      if (!new File(path).delete())
        Main.appendLog(this, s"削除に失敗しました: ${path}")
    }
    def deleteDirectory_(path: String): Unit = {
      val d = new File(path)
      d.listFiles.foreach { f =>
        if (f.isDirectory) deleteDirectory_(f.getPath) else deleteFile_(f.getPath) }
      if (!d.delete())
        Main.appendLog(this, s"削除に失敗しました: ${path}")
    }

    item match {
      case FileInfo(FullPath(path), _, _) => {
        Main.appendLog(this, s"削除します: ${path}")
        deleteFile_(path)
      }
      case DirectoryInfo(FullPath(path), _, _) => {
        Main.appendLog(this, s"削除します: ${path}")
        deleteDirectory_(path)
      }
    }
  }

  /**
    * ファイル/ディレクトリをペーストする
    *
    * @param item
    */
  def pasteFile(item: ItemInfo, destPath: String): Unit = {
    def getDestPath(path: String): String = {
      val f = new File(path)
      if (!f.exists())
        path
      else {
        var i = 2
        var path2 = s"${path}_${i}"
        var f2 = new File(path2)
        while (f2.exists()) {
          i += 1
          path2 = s"${path}_${i}"
          f2 = new File(path2)
        }
        path2
      }
    }

    def pasteFile_(srcPath: String, destPath: String): Unit = {
      val f = new File(srcPath)
      val newPath = getDestPath(FileUtils.combine(destPath, f.getName))
      val srcFile = Paths.get(srcPath)
      val destFile = Paths.get(newPath)
      println(s"pasteFile_: ${srcPath}, ${destPath}, ${newPath}")
      Main.appendLog(this, s"pasteFile_: ${srcPath}, ${destPath}, ${newPath}")
      try {
        Files.copy(srcFile, destFile)
      }
      catch {
        case ex: Exception =>
          Main.appendLog(this, s"ペーストに失敗しました: ${newPath}, ${ex}")
      }
    }
    def pasteDirectory_(srcPath: String, destPath: String): Unit = {
      val f = new File(srcPath)
      val newPath = FileUtils.combine(destPath, f.getName)
      val srcFile = Paths.get(srcPath)
      val destFile = Paths.get(newPath)
      println(s"pasteDirectory_: ${srcPath}, ${destPath}, ${newPath}")
      Main.appendLog(this, s"pasteDirectory_: ${srcPath}, ${destPath}, ${newPath}")
      try {
        Files.copy(srcFile, destFile)
      }
      catch {
        case ex: Exception =>
          Main.appendLog(this, s"ペーストに失敗しました: ${newPath}, ${ex}")
      }
      f.listFiles.foreach { f2 =>
        if (f2.isDirectory) {
          pasteDirectory_(f2.getPath, FileUtils.combine(destPath, srcFile.getFileName.toString))
        } else {
          pasteFile_(f2.getPath, FileUtils.combine(destPath, srcFile.getFileName.toString))
        }
      }
    }

    item match {
      case FileInfo(FullPath(path), _, _) => {
        Main.appendLog(this, s"ペーストします: ${path}")
        pasteFile_(path, destPath)
      }
      case DirectoryInfo(FullPath(path), _, _) => {
        Main.appendLog(this, s"ペーストします: ${path}")
        pasteDirectory_(path, destPath)
      }
    }
  }
}
