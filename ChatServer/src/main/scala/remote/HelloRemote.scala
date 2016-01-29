package remote

import akka.actor._
import common._


object HelloRemote extends App  {
  val system = ActorSystem("HelloRemoteSystem")
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  remoteActor ! "STARTSERVER"
}

class RemoteActor extends Actor {

  var clients: Map[String, ActorRef] = Map()

  def receive = {
    case "STARTSERVER" =>
        println("server started")

    case Login(user) =>
        clients.values foreach { _ ! ChatMessage("server", s"$user is now online") }

        //add new client to clients list
        clients = clients + (user -> sender)
        println(s"new client registered: $user")

    case UserListRequest(user) =>
      sender ! UserListResponse(clients.keys.toList diff List(user) mkString ", " )

    case ChatMessage(from, message) =>
        val response = "["+ from +"] " + message
        println(s"ChatMessage from client: $response")

        clients.values foreach { _ ! ChatMessage(from, message) }

    case PrivateChatMessage(from, to, message) =>
        clients get to match {
          case Some(recipient: ActorRef) => recipient ! ChatMessage(from, "Private message: " + message)
          case None => sender ! ChatMessage("server", s"User $to offline, message not delivered")
        }

    case _ =>
        println("something unexpected")
  }
}
