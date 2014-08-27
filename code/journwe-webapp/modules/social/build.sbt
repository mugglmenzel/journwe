import play.Play.autoImport._

name := "social"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  "com.feth" %% "play-authenticate" % "0.6.5-SNAPSHOT",
  "com.amazonaws" % "aws-java-sdk" % "1.7.5",
  "com.rosaloves" % "bitlyj" % "2.0.0",
  "com.restfb" % "restfb" % "1.6.12",
  "com.google.api-client" % "google-api-client" % "1.17.0-rc",
  "com.google.http-client" % "google-http-client" % "1.17.0-rc",
  "com.google.http-client" % "google-http-client-jackson2" % "1.17.0-rc",
  "com.google.apis" % "google-api-services-plus" % "v1-rev118-1.17.0-rc",
  "fi.foyt" % "foursquare-api" % "1.0.2" exclude("com.google.appengine", "appengine-api-1.0-sdk"), //exclude needed; appengine detroys sending emails with apache commons
  "org.twitter4j" % "twitter4j-core" % "3.0.5",
  "org.igniterealtime.smack" % "smack" % "3.2.1",
  "org.igniterealtime.smack" % "smackx" % "3.2.1"
)


resolvers ++= Seq(
  "play-easymail (release)" at "http://joscha.github.io/play-easymail/repo/releases/",
  "play-easymail (snapshot)" at "http://joscha.github.io/play-easymail/repo/snapshots/",
  "play-authenticate (release)" at "http://joscha.github.io/play-authenticate/repo/releases/",
  "play-authenticate (snapshot)" at "http://joscha.github.io/play-authenticate/repo/snapshots/",
  "Foursquare V2 API for Java Repository" at "http://foursquare-api-java.googlecode.com/svn/repository",
  "Twitter4J Repository" at "http://twitter4j.org/maven2"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)

doc in Compile <<= target.map(_ / "none")