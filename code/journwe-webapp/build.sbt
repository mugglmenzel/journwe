import play.Project._

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
  "com.mohiva" %% "play-html-compressor" % "0.2-SNAPSHOT"
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
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

playJavaSettings

doc in Compile <<= target.map(_ / "none")

// LESS

lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "css" ** "journwe.less")



// RequireJS

requireJs ++= Seq(
  "journwe.js",
  "header.js",
  "comments.js",
  "modules/index.js",
  "modules/indexCat.js",
  "modules/indexNew.js",
  "modules/indexVet.js",
  "modules/adventure.js",
  "modules/adventurePublic.js",
  "modules/inspiration.js",
  "modules/user.js"
)

requireJsShim += "build.js"

//requireNativePath := Some("java -cp lib/requireJs/js.jar:lib/requireJs/compiler.jar org.mozilla.javascript.tools.shell.Main lib/requireJs/r.js")

lazy val root = project.in(file(".")).aggregate(acl, admin, core, social).dependsOn(acl, admin, core, data, dataCache, social, email)

lazy val data = project.in(file("modules/data"))
lazy val dataCache = project.in(file("modules/dataCache")).aggregate(data).dependsOn(data)
lazy val core = project.in(file("modules/core")).aggregate(data, dataCache).dependsOn(data, dataCache)


lazy val social = project.in(file("modules/social")).aggregate(core).dependsOn(core, data, dataCache)
lazy val acl = project.in(file("modules/acl")).aggregate(core).dependsOn(core, data, dataCache)

lazy val admin = project.in(file("modules/admin")).aggregate(core).dependsOn(core, data, dataCache)

lazy val email = project.in(file("modules/email")).dependsOn(core, acl)