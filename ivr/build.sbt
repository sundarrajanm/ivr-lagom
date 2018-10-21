organization in ThisBuild := "com.experiment"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

lazy val `ivr` = (project in file("."))
  .aggregate(`ivr-api`, `ivr-impl`)

lazy val `ivr-api` = (project in file("ivr-api"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )

lazy val `ivr-impl` = (project in file("ivr-impl"))
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomLogback,
      lagomJavadslTestKit,
      lombok
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`ivr-api`, `usecase`, `dataprovider`)

lazy val `usecase` = (project in file("usecase"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lombok,
      "com.google.inject" % "guice" % "4.2.1"
    )
  )
  .dependsOn(`core`, `dataprovider`)

lazy val `dataprovider` = (project in file("dataprovider"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lombok
    )
  )
  .dependsOn(`core`)

lazy val `core` = (project in file("core"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lombok, apacheCommons, flogger, floggerBackend
    )
  )

val lombok = "org.projectlombok" % "lombok" % "1.18.2"
val apacheCommons = "org.apache.commons" % "commons-lang3" % "3.8.1"
val flogger = "com.google.flogger" % "flogger" % "0.3.1"
val floggerBackend = "com.google.flogger" % "flogger-system-backend" % "0.3.1"

def common = Seq(
  javacOptions in compile += "-parameters"
)
