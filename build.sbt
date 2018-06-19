name := "key-value-storage-cluster"

version := "0.1"

scalaVersion := "2.12.6"

val akkaVersion = "2.5.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "io.kamon" % "sigar-loader" % "1.6.6-rev002")

libraryDependencies += "com.typesafe.akka" %% "akka-distributed-data" % "2.5.13"