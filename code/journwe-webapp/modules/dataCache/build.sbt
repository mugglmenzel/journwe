import play.Play.autoImport._

name := "dataCache"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  cache,
  "com.github.mumoshu" %% "play2-memcached" % "0.6.0"
)

resolvers ++= Seq(
  // Repository for com.github.mumoshu play2-memcached
  // required to resolve `spymemcached`, the plugin's dependency.
  "Spy Repository" at "http://files.couchbase.com/maven2"
)


lazy val root = (project in file(".")).enablePlugins(PlayJava)

doc in Compile <<= target.map(_ / "none")