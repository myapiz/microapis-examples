val Http4sVersion = "0.23.26"
val CirceVersion = "0.14.9"
val MunitVersion = "1.0.0"
val LogbackVersion = "1.5.6"
val MunitCatsEffectVersion = "2.0.0"
val CatsVersion = "3.5.4"
val smithyVersion = "1.50.0"

val versions = new {
  val myapiz = "0.0.1-SNAPSHOT"
}

lazy val root = (project in file("."))
  .enablePlugins(PackPlugin)
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(
    organization := "com.myapiz",
    name := "microapis",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.4.2",
    scalacOptions := Seq(
      "-deprecation",
      "-feature",
      "-language:noAutoTupling",
      "-language:unsafeNulls",
      "-language:strictEquality"
    ),
    githubOwner := "myapiz",
    updateOptions := updateOptions.value.withLatestSnapshots(true),
    resolvers += Resolver.githubPackages("myapiz", "smithy"),
    libraryDependencies ++= Seq(
      // basic api
      "org.typelevel" %% "cats-effect" % CatsVersion,
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s" % smithy4sVersion.value,
      "software.amazon.smithy" % "smithy-model" % smithyVersion,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger" % smithy4sVersion.value,
      "com.disneystreaming.alloy" % "alloy-core" % "0.3.11",
      "io.circe" %% "circe-parser" % CirceVersion,
      "com.myapiz" % "smithy4s_3" % versions.myapiz,

      // Loggin
      "org.fusesource.jansi" % "jansi" % "2.4.1",
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      // otp
      "com.github.bastiaanjansen" % "otp-java" % "2.0.3",
      // TEST
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect" % MunitCatsEffectVersion % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / mainClass := Some("com.myapiz.microapis.Main")
  )
