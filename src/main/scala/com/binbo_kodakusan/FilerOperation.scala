package com.binbo_kodakusan

trait FilerOperation {
  def operationKeyHelp(): Unit

  def operationKeyTop(): Unit
  def operationKeyBottom(): Unit
  def operationKeyUp(): Unit
  def operationKeyDown(): Unit
  def operationKeyParent(): Unit
  def operationKeyChild(): Unit

  def operationKeyToggleMark(): Unit
  def operationKeyToggleAllMarks(): Unit
  def operationKeyVisualMode(): Unit
}
