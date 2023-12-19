import CiCommands.{ ciBuild, devBuild }

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.typelevel" %% "cats-effect" % "2.1.4",
  "com.oracle.ojdbc" % "ojdbc8" % "19.3.0.0",
  "com.github.pureconfig" %% "pureconfig" % "0.16.0",
  "eu.timepit" %% "refined" % "0.10.3",
  "eu.timepit" %% "refined-pureconfig" % "0.10.3",
  "org.scalamock" %% "scalamock" % "5.1.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalacheck" %% "scalacheck" % "1.15.4" % Test
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Ywarn-unused:imports",
  "-Ywarn-dead-code",
  "-Xlint:adapted-args",
  "-Xsource:2.13",
  "-Xfatal-warnings"
)

commands ++= Seq(ciBuild, devBuild)

scalafmtOnCompile := true
scalafmtConfig := file(".scalafmt.conf")
coverageFailOnMinimum := true
coverageMinimumStmtTotal := 100
