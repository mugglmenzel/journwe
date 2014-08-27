import play.Play.autoImport._

name := "email"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  "com.feth" %% "play-easymail" % "0.6.4-SNAPSHOT"
)

resolvers ++= Seq(
  "play-easymail (release)" at "http://joscha.github.io/play-easymail/repo/releases/",
  "play-easymail (snapshot)" at "http://joscha.github.io/play-easymail/repo/snapshots/"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)

doc in Compile <<= target.map(_ / "none")