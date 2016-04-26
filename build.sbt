inThisBuild(Seq(
  name := "ScalaJS Demo",
  version := "1.0",
  scalaVersion := "2.11.8",
  organization := "com.avsystem.demo",
  compileOrder := CompileOrder.Mixed,
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:implicitConversions",
    "-language:existentials",
    "-language:dynamics",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-Xfuture",
    "-Xfatal-warnings",
    "-Xlint:_,-missing-interpolator,-adapted-args"
  )
))

val silencerVersion = "0.4"
val scalatestVersion = "3.0.0-M16-SNAP4"
val avsCommonsVersion = "1.14.0"
val scalajsDomVersion = "0.9.0"
val scalajsJQueryVersion = "0.9.0"
val upickleVersion = "0.4.0"
val jqueryVersion = "2.2.3"
val jettyVersion = "9.3.8.v20160314"

val commonSettings = Seq(
  libraryDependencies += compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion),
  libraryDependencies ++= Seq(
    "com.github.ghik" % "silencer-lib" % silencerVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test
  ),
  ideBasePackages := Seq(organization.value)
)

lazy val root = project.in(file(".")).aggregate(demoJS, demoJVM)

lazy val demo = crossProject.in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.avsystem.commons" %%% "commons-shared" % avsCommonsVersion,
      "com.lihaoyi" %%% "upickle" % upickleVersion
    )
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.avsystem.commons" %% "commons-core" % avsCommonsVersion,
      "org.eclipse.jetty" % "jetty-server" % jettyVersion
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
      "be.doeraene" %%% "scalajs-jquery" % scalajsJQueryVersion
    ),
    jsDependencies ++= Seq(
      "org.webjars" % "jquery" % jqueryVersion / "jquery.js" minified "jquery.min.js"
    ),
    mainClass in Compile := Some("com.avsystem.demo.PadderClient"),
    persistLauncher := true
  )

lazy val demoJVM = demo.jvm
lazy val demoJS = demo.js
