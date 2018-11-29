package com.binbo_kodakusan

/**
  * ファイラコンテナ(左右のファイルリストコンテナのコンテナ)
  *
  * @param leftPath
  * @param rightPath
  */
class FilerContainer(path: String) {
  val Container = new FileListContainer(path)
  val Control = Container.Control
}
