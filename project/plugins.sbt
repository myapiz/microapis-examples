addSbtPlugin(("com.geirsson" % "sbt-scalafmt" % "1.5.1").cross(CrossVersion.full))
addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.20")

addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.1")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")
addSbtPlugin("com.disneystreaming.smithy4s" % "smithy4s-sbt-codegen" % "0.18.23")
addSbtPlugin("com.codecommit" % "sbt-github-packages" % "0.5.3")
