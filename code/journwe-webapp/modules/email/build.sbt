import play.Project._
import sbt.Keys._
import sbt._

name := "email"

version := "1.0"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  "com.feth" %% "play-easymail" % "0.5-SNAPSHOT"
)

resolvers ++= Seq(
  // Repository for easy mail (used in authenticate)
  Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns)
)

playJavaSettings

doc in Compile <<= target.map(_ / "none")