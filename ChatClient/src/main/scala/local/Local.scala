package local

import akka.actor.ActorSystem
import akka.actor.Props
import common._
import scala.util.control.Breaks._


object Local extends App {

  val greeting: String = "Use /login <name> to register at the server."
  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[Client], name = "Client")

  println(greeting)
  breakable {
    Stream.continually(Console.readLine(">>> ")).takeWhile( _ ne null) foreach { line =>
      if (line.startsWith("/login ")) {
        localActor ! Nickname(line.split(" ").last)
      } else if (line.startsWith("/logout")) {
        localActor ! Logout
        println("\rExiting...")
        system.stop(localActor)
        system.shutdown()
        break
      } else {
        localActor ! LocalMessage(line)
      }
    }
  }
}

