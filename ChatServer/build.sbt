name := "Akka Chat"

version := "1.0"

scalaVersion := "2.10.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.10" % "2.2-M1",
  "com.typesafe.akka" % "akka-remote_2.10" % "2.2-M1"
)
