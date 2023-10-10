import CiCommands.{ ciBuild, devBuild }

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

coverageMinimumStmtTotal := 100
coverageFailOnMinimum := true
