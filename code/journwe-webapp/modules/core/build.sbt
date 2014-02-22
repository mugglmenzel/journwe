import play.Project._

name := "core"

version := "1.0"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  "com.amazonaws" % "aws-java-sdk" % "1.7.1",
  "com.feth" %% "play-authenticate" % "0.5.2-SNAPSHOT",
  "com.ecwid" % "ecwid-mailchimp" % "1.3.0.5"
)



resolvers ++= Seq(
  // Repository for easy mail (used in authenticate)
  Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),
  // Repository for authenticate
  Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns),
  // Repository for com.github.mumoshu play2-memcached
  // required to resolve `spymemcached`, the plugin's dependency.
  "Spy Repository" at "http://files.couchbase.com/maven2"
)

playJavaSettings