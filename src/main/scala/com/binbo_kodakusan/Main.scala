package com.binbo_kodakusan

import scala.swing.{SplitPane, BoxPanel, Dimension, MainFrame, Orientation, ScrollPane, SimpleSwingApplication, Table, TextArea}

object Main extends SimpleSwingApplication {
  setSystemLookAndFeel()

  // ログ出力のため最初に作成されていないとダメ
  val LogArea: TextArea = createLogArea()
  val filerContainer = new FilerContainer(FileUtils.getHomeDirectory())

  def top = new MainFrame {
    title = "ScFiler"
    minimumSize = new Dimension(600, 400)

    contents = new SplitPane(
      Orientation.Horizontal,
      filerContainer.Control,
      new ScrollPane(LogArea))
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
