package remote

import akka.actor._
import common._


object HelloRemote extends App  {
  val system = ActorSystem("HelloRemoteSystem")
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  remoteActor ! "START"
}

class RemoteActor extends Actor {

  def receive = {
    case "START" =>
        println("The RemoteActor has started")

    case ChatMessage(from, message) =>
        val response = "["+ from +"] " + message
        println(response)
        sender ! ChatMessage(from, message)

    case _ =>
        println("something unexpected")
  }
}
