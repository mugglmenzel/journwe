import play.Project._


name := "data"

version := "1.0"

libraryDependencies ++= Seq(
  javaCore,
  javaEbean,
  "com.amazonaws" % "aws-java-sdk" % "1.7.1"
)



playJavaSettings

doc in Compile <<= target.map(_ / "none")