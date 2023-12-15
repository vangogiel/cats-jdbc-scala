import CiCommands.{ ciBuild, devBuild }

libraryDependencies ++= Seq(
  "com.oracle.ojdbc" % "ojdbc8" % "19.3.0.0",
  "com.github.pureconfig" %% "pureconfig" % "0.16.0",
  "eu.timepit" %% "refined" % "0.10.3",
  "eu.timepit" %% "refined-pureconfig" % "0.10.3",
  "org.scalamock" %% "scalamock" % "5.1.0" % Test,
  "org.scalactic" %% "scalactic" % "3.2.10" % Test,
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalacheck" %% "scalacheck" % "1.15.4" % Test,
  "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test
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

coverageFailOnMinimum := true
