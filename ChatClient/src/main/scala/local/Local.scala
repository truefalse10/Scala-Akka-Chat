package local

import akka.actor.ActorSystem
import common._


object Local extends App {

  val greeting: String = "Use /login <name> to register at the server."
  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[Client], name = "Client")

  println(greeting)
  Stream.continually(Console.readLine(">>> ")).takeWhile( _ ne null) foreach { line =>
    if (line.startsWith("/login ")) {
      localActor ! Nickname(line.split(" ").last)
    } else {
      localActor ! LocalMessage(line)
    }
  }
}

