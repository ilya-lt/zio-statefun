name := "zio-state"

version := "0.1"

scalaVersion := "2.13.6"

idePackagePrefix := Some("com.lightricks")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % "1.0.0.0-RC17",
  "org.apache.flink" % "statefun-sdk-java" % "3.0.0",
  "com.google.protobuf" % "protobuf-java" % "3.17.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)