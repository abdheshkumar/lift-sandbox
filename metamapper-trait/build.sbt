name := "lift-mappedmongo"

organization := "eltimn.com"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

{
  val liftVersion = "2.4"
  libraryDependencies ++= Seq(
    "net.liftweb" %% "lift-mongodb-record" % liftVersion,
    "net.liftweb" %% "lift-mapper" % liftVersion,
    "ch.qos.logback" % "logback-classic" % "1.0.0",
    "org.scalatest" %% "scalatest" % "1.8" % "test"
  )
}

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"
