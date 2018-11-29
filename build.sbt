ThisBuild / organization := "com.binbo-kodakusan"
ThisBuild / scalaVersion := "2.12.7"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "prototype_sc",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "2.0.3"
    ),
  )
