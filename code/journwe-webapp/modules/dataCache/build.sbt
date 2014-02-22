
name := "dataCache"

version := "1.0"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  cache,
  "com.github.mumoshu" %% "play2-memcached" % "0.3.0.2"
)

resolvers ++= Seq(
  // Repository for com.github.mumoshu play2-memcached
  // required to resolve `spymemcached`, the plugin's dependency.
  "Spy Repository" at "http://files.couchbase.com/maven2"
)


playJavaSettings