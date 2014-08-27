
name := "admin"

version := "1.0"

scalaVersion := "2.11.1"

resolvers ++= Seq(
  "play-easymail (release)" at "http://joscha.github.io/play-easymail/repo/releases/",
  "play-easymail (snapshot)" at "http://joscha.github.io/play-easymail/repo/snapshots/",
  "play-authenticate (release)" at "http://joscha.github.io/play-authenticate/repo/releases/",
  "play-authenticate (snapshot)" at "http://joscha.github.io/play-authenticate/repo/snapshots/"
)


lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

doc in Compile <<= target.map(_ / "none")