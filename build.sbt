ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"

val fs2DataVersion = "1.13.0"
val declineVersion = "2.6.1"
val http4sVersion  = "0.23.33"

lazy val root = (project in file("."))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    name := "data-knife",
    libraryDependencies ++= Seq(
      "org.gnieh"    %%% "fs2-data-json"       % fs2DataVersion,
      "org.gnieh"    %%% "fs2-data-cbor"       % fs2DataVersion,
      "org.gnieh"    %%% "fs2-data-cbor-json"  % fs2DataVersion,
      "co.fs2"       %%% "fs2-io"              % "3.13.0",
      "com.monovore" %%% "decline-effect"      % declineVersion,
      //"org.http4s"   %%% "http4s-ember-client" % http4sVersion,
    ),
  )
