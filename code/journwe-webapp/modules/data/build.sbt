name := "data"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.7.5"
)


lazy val root = (project in file(".")).enablePlugins(PlayJava)

doc in Compile <<= target.map(_ / "none")