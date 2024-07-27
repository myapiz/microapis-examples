val Http4sVersion = "0.23.26"
val CirceVersion = "0.14.9"
val MunitVersion = "1.0.0"
val LogbackVersion = "1.5.6"
val MunitCatsEffectVersion = "2.0.0"
import smithy4s.codegen.Smithy4sCodegenPlugin

val CatsVersion = "3.5.4"

lazy val root = (project in file("."))
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
      "-language:strictEquality",
      "-Werror",
      "-Wunused:all",
      "-Xfatal-warnings",
      "-Yexplicit-nulls",
      "-Ysafe-init"
    ),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % CatsVersion,
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s" % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger" % smithy4sVersion.value,
      // otp
      "com.github.bastiaanjansen" % "otp-java" % "2.0.3",
      // Loggin
      "org.fusesource.jansi" % "jansi" % "2.4.1",
      // TEST
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect" % MunitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion % Runtime
    ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x                   => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )
