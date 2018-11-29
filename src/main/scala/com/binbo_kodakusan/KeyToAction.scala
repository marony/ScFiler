package com.binbo_kodakusan

import scala.swing.event.Key

/**
  * アクション情報
  */
sealed trait ActionInfo
case class NoKeyAction (vs: Seq[KeyInfo], candidates: Seq[KeyToActionMapping]) extends ActionInfo
case class KeyAction(vs: Seq[KeyInfo], k: KeyToActionMapping) extends ActionInfo

/**
  * キー情報
  *
  * @param c
  * @param m
  */
case class KeyInfo(c: Char, m: Key.Modifiers)
object KeyInfo {
  def apply(c: Char, m: Key.Modifiers) = new KeyInfo(c, m)
}

/**
  * キーボードショートカットとアクションの対応
  */
case class KeyToActionMapping(keys: Seq[KeyInfo], f: () => Unit, help: String)

/**
  * キーボードショートカットとアクションの対応を扱う
  */
class KeyToAction[T <: FilerOperation](o: T) {
  // 対応の定義
  def actions =
    Seq(
      KeyToActionMapping(
        Seq(
          KeyInfo('?', 0),
        ),
        () => o.operationKeyHelp,
        "ヘルプ"
      ),
      KeyToActionMapping(
        Seq(
          KeyInfo('g', 0),
          KeyInfo('g', 0),
        ),
        () => o.operationKeyTop(),
        "カーソルを先頭に移動"
      ),
      KeyToActionMapping(
        Seq(
          KeyInfo('G', 0),
        ),
        () => o.operationKeyBottom(),
        "カーソルを最後に移動"
      ),
      KeyToActionMapping(
        Seq(
          KeyInfo('j', 0),
        ),
        () => o.operationKeyDown(),
        "カーソルを下に移動"
      ),
      KeyToActionMapping(
        Seq(
          KeyInfo('k', 0),
        ),
        () => o.operationKeyUp(),
        "カーソルを上に移動"
      ),
      KeyToActionMapping(
        Seq(
          KeyInfo('h', 0),
        ),
        () => o.operationKeyParent(),
        "親に移動"
      ),
      KeyToActionMapping(
        Seq(
          KeyInfo('l', 0),
        ),
        () => o.operationKeyChild(),
        "子に移動"
      ),
    )

  /**
    * キーボードショートカットからアクションを返す
    * 
    * @param vs
    * @param c
    * @param m
    * @tparam T
    * @return
    */
  def nextKey(vs: Seq[KeyInfo], c: Char, m: Key.Modifiers): ActionInfo = {
    val k = KeyInfo(c, m)
    val vss = vs :+ k

    Main.appendLog(this, s"nextKey: ${c}, ${m}, ${Key.Escape}")
    if (c.toInt == Key.Escape) {
      Main.appendLog(this, "ESCAPE")
      return NoKeyAction(Seq.empty, Seq.empty)
    }

    // 対応するアクションを検索
    val keys = actions.filter(_.keys == vss)
    // 最大のキーボードショートカットの文字数
    val maxKeys = actions.map(_.keys.length).max
    // 今入力されているキーボードショートカットと部分一致するアクション
    val partiallyMatch = actions.filter(_.keys.take(vss.length) == vss)
    if (keys.nonEmpty) {
      // 一致したのでアクションを返す
      val r = keys.head
      KeyAction(Seq.empty, r)
    } else if (vss.length >= maxKeys) {
      // 最大のキーボードショートカットより長いキー入力なので対応なし
      NoKeyAction(Seq.empty, Seq.empty)
    } else if (partiallyMatch.isEmpty) {
      // 部分一致するショートカットがないので対応なし
      NoKeyAction(Seq.empty, Seq.empty)
    } else {
      // 部分一致しているので、ヘルプ表示用に候補を返す
      NoKeyAction(vss, partiallyMatch)
    }
  }
}
