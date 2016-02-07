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

    case ValidateName(name) =>
      if ((name startsWith "/login") || name == null || name.isEmpty)
        sender ! "Invalid"
      else if (clients.keys.exists(_ == name))
        sender ! "Dupe"
      else {
        sender ! "Ok"
      }

    case Login(name) =>
      // notify other users
      clients.values foreach { _ ! Notify(s"$name is now online") }

      //add new client to clients map
      clients = clients + (name -> sender)
      println(s"new client registered: $name")

    case UserListRequest(user) =>
      sender ! UserListResponse(clients.keys.toList diff List(user) mkString ", " )

    case ChatMessage(from, message) =>
        val response = s"[$from] $message"
        println(s"ChatMessage from client: $response")

        clients.values foreach { _ ! PublicMessage(from, message) }

    case PrivateChatMessage(from, to, message) =>
        clients get to match {
          case Some(recipient: ActorRef) => recipient ! PrivateMessage(from, message)
          case None => sender ! Notify(s"User $to offline, message not delivered")
        }

    case _ =>
        println("something unexpected")
  }
}
