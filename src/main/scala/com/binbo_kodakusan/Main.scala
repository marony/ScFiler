package com.binbo_kodakusan

import scala.swing.{BoxPanel, Dimension, MainFrame, Orientation, ScrollPane, SimpleSwingApplication, Table, TextArea}

object Main extends SimpleSwingApplication {
  setSystemLookAndFeel()

  // ログ出力のため最初に作成されていないとダメ
  val LogArea: TextArea = createLogArea()
  val filerContainer = new FilerContainer(FileUtils.getHomeDirectory())

  def top = new MainFrame {
    title = "ScFiler"
    minimumSize = new Dimension(300, 200)

    contents = new BoxPanel(Orientation.Vertical) {
      contents += filerContainer.Control
      contents += new ScrollPane(LogArea)
    }
  }

  def createLogArea(): TextArea = {
    new TextArea() {
      maximumSize = new Dimension(Short.MaxValue, 5000)
      editable = false
    }
  }

  def appendLog(source: AnyRef, log: String): Unit = {
    LogArea.text = LogArea.text + "\r\n" + log
  }

  def setSystemLookAndFeel() {
    import javax.swing.UIManager
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  }
}
