ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.9"

lazy val root = (project in file("."))
  .settings(
    name := "traffic-counter",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % "3.2.12",
      "org.apache.logging.log4j" % "log4j-api" % "2.19.0",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.19.0",
      "org.apache.logging.log4j" % "log4j-core" % "2.19.0",
      "org.scalactic" %% "scalactic" % "3.2.13" % Test,
      "org.scalatest" %% "scalatest" % "3.2.13" % Test
    )

  )
