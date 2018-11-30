package com.binbo_kodakusan

import javax.swing.table.{AbstractTableModel, DefaultTableModel, TableColumn}

import scala.swing.{BoxPanel, Component, Dimension, Orientation, ScrollPane, Table, TextArea, TextField}
import scala.swing.event.{EditDone, Key, KeyTyped, TableRowsSelected}
import java.io.File

/**
  * ディレクトリパスエディットとファイルリストのコンテナ
  *
  * @param path 初期パス
  */
class FileListContainer(var path: String) extends FilerOperation {
  private[this] val keyToAction = new KeyToAction(this)
  private[this] var keys = Seq.empty[KeyInfo]

  private[this] var centerPath: String = FileUtils.getHomeDirectory
  private[this] var centorFileList: Seq[ItemInfo] = IndexedSeq.empty

  val Edit: TextField = createPathTextBox(path)
  val LeftList: Table = createFileList()
  val CenterList: Table = createFileList()
  val RightList: Table = createFileList()
  val Control: Component = new BoxPanel(Orientation.Vertical) {
    contents += Edit
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new ScrollPane(LeftList)
      contents += new ScrollPane(CenterList)
      contents += new ScrollPane(RightList)
    }
  }

  // カラムの定義
  val LeftColumns = Vector("カラムA")
  val CenterColumns = Vector("カラムA", "カラムB")
  val RightColumns = Vector("カラムA")

  // 初期表示
  setPathToLists(path)

  /**
    * 左中右のリストにファイル一覧を表示
    * @param path
    */
  private[this] def setPathToLists(path: String): Unit = {
    Edit.text = path
    centerPath = path
    val files = FileUtils.getDirectoryContents(path)
    centorFileList = files
    // 真ん中
    FileListContainer.setPath(files, CenterList, CenterColumns)
    // 左右
    centerSelectionToLeftAndRightList(path, files)
  }

  /**
    * 現在のファイル内容でリストを再描画する
    */
  private[this] def redrawCenterList(): Unit = {
    // 選択範囲を保存する
    val selection = CenterList.selection
    val cells = selection.cells.clone()
    Main.appendLog(this, s"cells = ${cells}")
    // 真ん中
    FileListContainer.setPath(centorFileList, CenterList, CenterColumns)
    // 選択範囲を復活する
    cells.foreach { case (row, column) =>
      CenterList.peer.changeSelection(row, 0, false, false) }
  }

  /**
    * 真ん中のリストの選択行から左右のリストのファイル一覧を表示
    *
    * @param path
    * @param traversable
    */
  private[this] def centerSelectionToLeftAndRightList(path: String, files: Seq[ItemInfo]): Unit = {
    {
      // 左(親ディレクトリ)
      val parentPath = new File(path).getParent
      val parentFiles = FileUtils.getDirectoryContents(parentPath)
      FileListContainer.setPath(parentFiles, LeftList, LeftColumns)
    }
    {
      // 右(選択されているディレクトリの中身)
      val cells = CenterList.selection.cells
      if (cells.isEmpty)
        cells += ((0, 0))
      // 単一行選択なので1度しか実行されない
      cells.foreach { case (row, column) => {
        val selectedFile = files(row)
        val childFiles = selectedFile match {
          case DirectoryInfo(FullPath(path), FileName(FileUtils.ParentPath), _) =>
            Seq.empty[ItemInfo]
          case DirectoryInfo(FullPath(path), FileName(name), _) =>
            FileUtils.getDirectoryContents(path)
          case _ => Seq.empty[ItemInfo]
        }
        FileListContainer.setPath(childFiles, RightList, RightColumns)
      }}
    }
  }

  def operationKeyHelp(): Unit = {
    keyToAction.actions.foreach(k => Main.appendLog(this, k.toString))
  }

  def operationKeyTop(): Unit = {
    if (centorFileList.nonEmpty) {
      CenterList.peer.changeSelection(0, 0, false, false)
      centerSelectionToLeftAndRightList(centerPath, centorFileList)
    }
  }

  def operationKeyBottom(): Unit = {
    if (centorFileList.nonEmpty) {
      CenterList.peer.changeSelection(centorFileList.length - 1, 0, false, false)
      centerSelectionToLeftAndRightList(centerPath, centorFileList)
    }
  }

  def operationKeyUp(): Unit = {
    val cells = CenterList.selection.cells
    if (centorFileList.nonEmpty && cells.nonEmpty) {
      cells.foreach { case (row, column) => {
        if (row > 0) {
          CenterList.peer.changeSelection(row - 1, 0, false, false)
          centerSelectionToLeftAndRightList(centerPath, centorFileList)
        }
      }}
    }
  }

  def operationKeyDown(): Unit = {
    val cells = CenterList.selection.cells
    if (centorFileList.nonEmpty && cells.nonEmpty) {
      cells.foreach { case (row, column) => {
        if (row < centorFileList.length - 1) {
          CenterList.peer.changeSelection(row + 1, 0, false, false)
          centerSelectionToLeftAndRightList(centerPath, centorFileList)
        }
      }}
    }
  }

  def operationKeyParent(): Unit = {
    val p = new File(centerPath)
    if (p.getParent != null) {
      setPathToLists(p.getParent)
    }
  }

  def operationKeyChild(): Unit = {
    val cells = CenterList.selection.cells
    if (centorFileList.nonEmpty && cells.nonEmpty) {
      cells.foreach { case (row, column) => {
        val selectedFile = centorFileList(row)
        selectedFile match {
          case DirectoryInfo(FullPath(path), FileName(name), _) =>
            setPathToLists(path)
          case _ => // ファイルなので何もしない
        }
      }}
    }
  }

  def operationKeyToggleMark(): Unit = {
    val cells = CenterList.selection.cells
    if (centorFileList.nonEmpty && cells.nonEmpty) {
      cells.foreach { case (row, column) => {
        val selectedFile = centorFileList(row)
        selectedFile match {
          case FileInfo(path, name, mark) =>
            centorFileList = centorFileList.updated(row, FileInfo(path, name, Mark(!mark.value)))
          case DirectoryInfo(path, name, mark) =>
            centorFileList = centorFileList.updated(row, DirectoryInfo(path, name, Mark(!mark.value)))
        }
      }}
      redrawCenterList()
    }
  }

  def operationKeyToggleAllMarks(): Unit = {
    if (centorFileList.nonEmpty) {
      centorFileList.zipWithIndex.foreach { case (file, row) => {
        file match {
          case FileInfo(path, name, mark) =>
            centorFileList = centorFileList.updated(row, FileInfo(path, name, Mark(!mark.value)))
          case DirectoryInfo(path, name, mark) =>
            centorFileList = centorFileList.updated(row, DirectoryInfo(path, name, Mark(!mark.value)))
        }
      }}
      redrawCenterList()
    }
  }

  def operationKeyVisualMode(): Unit = {
    ???
  }

  /**
    * ディレクトリパスエディットを作成する
    *
    * @return コントロール
    */
  private[this] def createPathTextBox(path: String): TextField = {
    new TextField(path) {
      maximumSize = new Dimension(Short.MaxValue, 100)

      // events
      listenTo(this)
      reactions += {
        case EditDone(source) => onEditDone(source)
        case e => Main.appendLog(this, e.getClass.toString)
      }
    }
  }

  /**
    * ファイルリストの作成
    *
    * @return コントロール
    */
  private[this] def createFileList(): Table = {
    val table = new Table() {
      minimumSize = new Dimension(Short.MaxValue, 200)

      autoResizeMode = Table.AutoResizeMode.AllColumns
      selection.intervalMode = Table.IntervalMode.Single
      selection.elementMode = Table.ElementMode.Row

      // イベント処理
      listenTo(this.keys, this.selection)
      reactions += {
        case TableRowsSelected(source, range, adjusting) => onRowSelected(source, range, adjusting)
        case KeyTyped(source, c, modifiers, location) => onKeyDown(source, c, modifiers)
        case e => Main.appendLog(this, e.getClass.toString)
      }
    }
    // テーブルを編集不可にする
    val model = new DefaultTableModel {
      override def isCellEditable(row: Int, column: Int): Boolean = false
    }
    table.peer.setModel(model)

    table
  }

  /**
    * ディレクトリパスの編集が終了した
    *
    * @param source
    */
  private[this] def onEditDone(source: Component): Unit = {
    val path = Edit.text
    Main.appendLog(this, s"onEditDone: ${path}")
    setPathToLists(Edit.text)
  }

  /**
    * ファイルリストで選択が変更された
    *
    * @param table
    * @param range
    * @param adjusting
    */
  private[this] def onRowSelected(table: Table, range: Range, adjusting: Boolean): Unit = {
    Main.appendLog(this, s"onRowSelected: ${range}, ${adjusting}")
    if (table == CenterList)
      centerSelectionToLeftAndRightList(centerPath, centorFileList)
  }

  /**
    * ファイルリストでキーが押された
    *
    * @param source
    * @param c
    * @param m
    */
  private[this] def onKeyDown(source: Component, c: Char, m: Key.Modifiers): Unit = {
    Main.appendLog(this, s"onKeyDown: ${c}, ${c.toInt}, ${m}")
    // 押されたキーからアクションを取得
    keys = keyToAction.nextKey(keys, c, m) match {
      case NoKeyAction(vs, cs) => {
        Main.appendLog(this, s"NoKeyAction: ${vs}")
        cs.foreach( c => {
          Main.appendLog(this, c.help)
        })
        vs
      }
      case KeyAction(vs, k) => {
        Main.appendLog(this, s"KeyAction: ${vs}, ${k.help}")
        k.f()
        vs
      }
    }
  }
}

object FileListContainer {
  /**
    * ItemInfoを表示用に変換
    */
  private def convertFileList(files: Seq[ItemInfo]): Array[Array[AnyRef]] = {
    files.map {
      case FileInfo(FullPath(path), FileName(name), Mark(false)) =>
        Array(name, "FILE")
      case FileInfo(FullPath(path), FileName(name), Mark(true)) =>
        Array("* " + name, "FILE")
      case DirectoryInfo(FullPath(path), FileName(name), Mark(false)) =>
        Array(name, "DIRECTORY")
      case DirectoryInfo(FullPath(path), FileName(name), Mark(true)) =>
        Array("* " + name, "DIRECTORY")
    }.toArray.asInstanceOf[Array[Array[AnyRef]]]
  }

  /**
    * パスを設定してファイルリストを表示する
    *
    * @param path
    */
  private def setPath(files: Seq[ItemInfo], list: Table, columns: Vector[String]): Unit = {
    val files_ = convertFileList(files)
    val columns_ = columns.toArray.asInstanceOf[Array[AnyRef]]
    val model = list.model.asInstanceOf[javax.swing.table.DefaultTableModel]
    model.setDataVector(files_, columns_)
  }
}
