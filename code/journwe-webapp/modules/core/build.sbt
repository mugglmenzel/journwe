import play.Play.autoImport._

name := "core"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  "com.amazonaws" % "aws-java-sdk" % "1.7.5",
  "com.feth" %% "play-authenticate" % "0.6.5-SNAPSHOT",
  "com.ecwid" % "ecwid-mailchimp" % "1.3.0.5"
)



resolvers ++= Seq(
  "play-easymail (release)" at "http://joscha.github.io/play-easymail/repo/releases/",
  "play-easymail (snapshot)" at "http://joscha.github.io/play-easymail/repo/snapshots/",
  "play-authenticate (release)" at "http://joscha.github.io/play-authenticate/repo/releases/",
  "play-authenticate (snapshot)" at "http://joscha.github.io/play-authenticate/repo/snapshots/",
  // Repository for com.github.mumoshu play2-memcached
  // required to resolve `spymemcached`, the plugin's dependency.
  "Spy Repository" at "http://files.couchbase.com/maven2"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

doc in Compile <<= target.map(_ / "none")
