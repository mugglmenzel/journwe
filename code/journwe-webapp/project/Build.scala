import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "journwe-webapp"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "com.amazonaws" % "aws-java-sdk" % "1.3.33"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
