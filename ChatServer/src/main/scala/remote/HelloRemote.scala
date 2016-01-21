package remote

import akka.actor._
import common._


object HelloRemote extends App  {
  val system = ActorSystem("HelloRemoteSystem")
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  remoteActor ! "STARTSERVER"
}

class RemoteActor extends Actor {

  var clients: List[ActorRef] = List()

  def receive = {
    case "STARTSERVER" =>
        println("server started")

    case Login(user) =>
        //add new client to clients list
        clients:+sender
        println(s"new client registered: $user")

    case ChatMessage(from, message) =>
        val response = "["+ from +"] " + message
        println("ChatMessage from client: " + response)
        for (client <- clients) {
          client ! ChatMessage(from, message)
        }

    case _ =>
        println("something unexpected")
  }
}
