import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "journwe-webapp"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    // javaJdbc,
    javaEbean,
    "com.amazonaws" % "aws-java-sdk" % "1.4.2.1",
    "com.feth"    %% "play-authenticate" % "0.3.3-SNAPSHOT",
    "com.restfb" % "restfb" % "1.6.12",
    "com.ecwid" % "ecwid-mailchimp" % "1.3.0.5",
    "com.rosaloves" % "bitlyj" % "2.0.0",
    "org.igniterealtime.smack" % "smack" % "3.2.1",
    "org.igniterealtime.smack" % "smackx" % "3.2.1",
    "com.github.mumoshu" %% "play2-memcached" % "0.3.0.2"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here    

    // Repository for easy mail (used in authenticate)
    resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),
    // Repository for authenticate
    resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns),
    // Repository for com.github.mumoshu play2-memcached
    // required to resolve `spymemcached`, the plugin's dependency.
    resolvers += "Spy Repository" at "http://files.couchbase.com/maven2"
  )

}
