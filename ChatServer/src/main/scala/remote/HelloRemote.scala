package remote

import akka.actor._
import common._


object HelloRemote extends App  {
  val system = ActorSystem("HelloRemoteSystem")
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  remoteActor ! "STARTSERVER"
}

class RemoteActor extends Actor {

  var clients: Map[ActorRef, String] = Map()

  def receive = {
    case "STARTSERVER" =>
        println("server started")

    case Login(user) =>
        clients.keys foreach { _ ! ChatMessage("server", "New user online: " + user) }

        //add new client to clients list
        clients = clients + (sender -> user)
        println(s"new client registered: $user")

    case UserListRequest(user) =>
      sender ! UserListResponse(clients.values.toList diff List(user) mkString ", " )

    case ChatMessage(from, message) =>
        val response = "["+ from +"] " + message
        println(s"ChatMessage from client: $response")

        clients.keys foreach { _ ! ChatMessage(from, message) }

    case _ =>
        println("something unexpected")
  }
}
