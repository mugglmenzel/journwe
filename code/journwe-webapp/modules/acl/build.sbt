import play.Project._
import sbt.Keys._

name := "acl"

version := "1.0"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  cache,
  "com.feth" %% "play-authenticate" % "0.5.2-SNAPSHOT"
)

resolvers ++= Seq(
  // Repository for easy mail (used in authenticate)
  Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),
  // Repository for authenticate
  Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns)
)

playJavaSettings