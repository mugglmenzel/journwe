import sbt._
import play.Play.autoImport._
import PlayKeys._
import WebJs._

name := "journwe-webapp"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  cache,
  filters,
  "com.github.mumoshu" %% "play2-memcached" % "0.6.0",
  "com.amazonaws" % "aws-java-sdk" % "1.7.5",
  "com.feth" %% "play-authenticate" % "0.6.5-SNAPSHOT",
  "com.rosaloves" % "bitlyj" % "2.0.0",
  "org.apache.pdfbox" % "pdfbox" % "1.8.2",
  "commons-io" % "commons-io" % "2.4",
  "com.mohiva" %% "play-html-compressor" % "0.3"
)




resolvers ++= Seq(
  "play-easymail (release)" at "http://joscha.github.io/play-easymail/repo/releases/",
  "play-easymail (snapshot)" at "http://joscha.github.io/play-easymail/repo/snapshots/",
  "play-authenticate (release)" at "http://joscha.github.io/play-authenticate/repo/releases/",
  "play-authenticate (snapshot)" at "http://joscha.github.io/play-authenticate/repo/snapshots/",
  // Repository for com.github.mumoshu play2-memcached
  // required to resolve `spymemcached`, the plugin's dependency.
  "Spy Repository" at "http://files.couchbase.com/maven2",
  Resolver.sonatypeRepo("snapshots")
)

doc in Compile <<= target.map(_ / "none")

// LESS
LessKeys.verbose in Assets := true

LessKeys.compress in Assets := true

//LessKeys.cleancss in Assets := true           not working yet

includeFilter in (Assets, LessKeys.less) := "journwe.less"

// RequireJS
RjsKeys.mainModule := "static"

RjsKeys.mainConfig := "shim"

RjsKeys.modules += JS.Object("name" -> "adventure")

RjsKeys.modules += JS.Object("name" -> "adventurePublic")

RjsKeys.modules += JS.Object("name" -> "index")

RjsKeys.modules += JS.Object("name" -> "indexCat")

RjsKeys.modules += JS.Object("name" -> "indexNew")

RjsKeys.modules += JS.Object("name" -> "indexVet")

RjsKeys.modules += JS.Object("name" -> "inspiration")

RjsKeys.modules += JS.Object("name" -> "landing")

RjsKeys.modules += JS.Object("name" -> "user")




// Assets processing
pipelineStages := Seq(rjs, digest, gzip)


//requireNativePath := Some("java -cp lib/requireJs/js.jar:lib/requireJs/compiler.jar org.mozilla.javascript.tools.shell.Main lib/requireJs/r.js")

lazy val root = (project in file(".")).aggregate(acl, admin, social).dependsOn(acl, admin, core, data, dataCache, email, social).enablePlugins(PlayJava, SbtWeb)


lazy val social = (project in file("modules/social")).aggregate(core).dependsOn(core, data, dataCache).enablePlugins(PlayJava, SbtWeb)

lazy val acl = (project in file("modules/acl")).aggregate(core).dependsOn(core, data, dataCache).enablePlugins(PlayJava)

lazy val admin = (project in file("modules/admin")).aggregate(core).dependsOn(core, data, dataCache).enablePlugins(PlayJava, SbtWeb)


lazy val core = (project in file("modules/core")).aggregate(dataCache, email).dependsOn(data, dataCache, email).enablePlugins(PlayJava, SbtWeb)


lazy val email = (project in file("modules/email")).aggregate(data).dependsOn(data).enablePlugins(PlayJava)

lazy val dataCache = (project in file("modules/dataCache")).aggregate(data).dependsOn(data).enablePlugins(PlayJava)

lazy val data = (project in file("modules/data")).enablePlugins(PlayJava)


