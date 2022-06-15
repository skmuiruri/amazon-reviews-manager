import Dependencies._

ThisBuild / organization := "com.credimi"
ThisBuild / version := "0.0.1"
ThisBuild / scalaVersion := "2.13.7"
ThisBuild / homepage := Some(url("https://github.com/skmuiruri/amazon-reviews-manager"))
ThisBuild / description := "Scala coding challenge - Amazon reviews"

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

mainClass in (Compile, run) := Some("com.arm.Main")

lazy val root = (project in file("."))
  .settings(
    name := "amazon-reviews-manager",
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    libraryDependencies ++= Seq(
      dev.zio.zio,
      dev.zio.streams,
      dev.zio.test,
      dev.zio.testSbt,
      dev.zio.logging,
      dev.zio.config,
      dev.zio.configTypesafe,
      dev.zio.json,
      dev.zio.prelude,
      org.apache.logging.log4j.core,
      org.apache.logging.log4j.slf4jImlp,
      ioo.d11.zhttp
    )
  )