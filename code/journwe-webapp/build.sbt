import play.Project._
import sbt._

name := "journwe-webapp"

version := "1.0"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  cache,
  filters,
  "com.github.mumoshu" %% "play2-memcached" % "0.3.0.2",
  "com.amazonaws" % "aws-java-sdk" % "1.7.5",
  "com.feth" %% "play-authenticate" % "0.5.2-SNAPSHOT",
  "com.rosaloves" % "bitlyj" % "2.0.0",
  "org.apache.pdfbox" % "pdfbox" % "1.8.2",
  "commons-io" % "commons-io" % "2.4",
  "com.mohiva" %% "play-html-compressor" % "0.2.1"
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
  Resolver.sonatypeRepo("snapshots")
)

playJavaSettings

doc in Compile <<= target.map(_ / "none")

// LESS

lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "css" ** "journwe.less")



// RequireJS

requireJs ++= Seq(
  "adventure.js",
  "adventurePublic.js",
  "index.js",
  "indexCat.js",
  "indexNew.js",
  "indexVet.js",
  "inspiration.js",
  "landing.js",
  "user.js",
  "static.js"
)

requireJsShim += "shim.js"

//requireNativePath := Some("java -cp lib/requireJs/js.jar:lib/requireJs/compiler.jar org.mozilla.javascript.tools.shell.Main lib/requireJs/r.js")

lazy val root = project.in(file(".")).aggregate(acl, admin, social).dependsOn(acl, admin, core, data, dataCache, email, social)


lazy val social = project.in(file("modules/social")).aggregate(core).dependsOn(core, data, dataCache)

lazy val acl = project.in(file("modules/acl")).aggregate(core).dependsOn(core, data, dataCache)

lazy val admin = project.in(file("modules/admin")).aggregate(core).dependsOn(core, data, dataCache)


lazy val core = project.in(file("modules/core")).aggregate(dataCache, email).dependsOn(data, dataCache, email)


lazy val email = project.in(file("modules/email")).aggregate(data).dependsOn(data)

lazy val dataCache = project.in(file("modules/dataCache")).aggregate(data).dependsOn(data)

lazy val data = project.in(file("modules/data"))


