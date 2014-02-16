import play.Project._

name := "journwe-webapp"

version := "1.0"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  cache,
  "com.amazonaws" % "aws-java-sdk" % "1.7.1",
  "com.feth" %% "play-authenticate" % "0.5.2-SNAPSHOT",
  "com.restfb" % "restfb" % "1.6.12",
  "com.google.api-client" % "google-api-client" % "1.17.0-rc",
  "com.google.http-client" % "google-http-client" % "1.17.0-rc",
  "com.google.http-client" % "google-http-client-jackson2" % "1.17.0-rc",
  "com.google.apis" % "google-api-services-plus" % "v1-rev118-1.17.0-rc",
  "com.google.apis" % "google-api-services-plusDomains" % "v1-rev21-1.17.0-rc",
  "fi.foyt" % "foursquare-api" % "1.0.2",
  "org.twitter4j" % "twitter4j-core" % "3.0.5",
  "com.ecwid" % "ecwid-mailchimp" % "1.3.0.5",
  "com.rosaloves" % "bitlyj" % "2.0.0",
  "org.igniterealtime.smack" % "smack" % "3.2.1",
  "org.igniterealtime.smack" % "smackx" % "3.2.1",
  "com.github.mumoshu" %% "play2-memcached" % "0.3.0.2",
  "org.apache.pdfbox" % "pdfbox" % "1.8.2"
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
  "Spy Repository" at "http://files.couchbase.com/maven2",
  "Foursquare V2 API for Java Repository" at "http://foursquare-api-java.googlecode.com/svn/repository",
  "Twitter4J Repository" at "http://twitter4j.org/maven2"
)

playJavaSettings

requireJs ++= Seq(
  "main.js",
  "adventure.js",
  "inspiration.js",
  "user.js",
  "build.js"
)

requireJsShim += "build.js"