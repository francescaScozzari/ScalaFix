// Build environment

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint", "-Xlint:-missing-interpolator")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % Test,
  "org.scalacheck" %% "scalacheck" % "1.12.5" % Test
)

// Metadata

name := "ScalaFix"

version := "0.9-SNAPSHOT"

description := "A Scala library for solving fixpoint equations"

organization := "it.unich.scalafix"

licenses := Seq("GPL-3.0" -> url("https://opensource.org/licenses/GPL-3.0"))

homepage := Some(url("https://github.com/jandom-devel/ScalaFix"))

startYear := Some(2015)

developers := List(
  new Developer(
    "amato",
    "Gianluca Amato", "gianluca.amato.74@unich.it",
    url("http://www.sci.unich.it/~amato/")
  )
)

scmInfo := Some(new ScmInfo(
  url("https://github.com/jandom-devel/ScalaFix"),
  "scm:git:https://github.com/jandom-devel/ScalaFix.git",
  Some("scm:git:https://github.com/jandom-devel/ScalaFix.git")
))

// Eclipse plugin

EclipseKeys.eclipseOutput := Some("target.eclipse")

// sbt-ide plugin

ideOutputDirectory in Compile := Some(new File("target/idea/classes"))
ideOutputDirectory in Test := Some(new File("target/idea/test-classes"))

