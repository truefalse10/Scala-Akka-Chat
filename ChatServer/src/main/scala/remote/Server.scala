package remote

import akka.actor._
import common._
import java.util.Calendar
import java.text.SimpleDateFormat

class Server extends Actor {

  var clients: Clients[ActorRef] = new Clients()

  def receive = {
    case "STARTSERVER" =>
        println("server started")

    case ValidateName(name) =>
      if ((name startsWith "/login") || name == null || name.isEmpty)
        sender ! "Invalid"
      else if (clients.connected(name))
        sender ! "Dupe"
      else {
        sender ! "Ok"
      }

    case Login(name) =>
      // notify other users
      clients foreach { _.reference ! Notify(s"$name is now online") }

      //add new client to clients map
      clients.connect(name, sender)
      println(s"new client registered: $name")

    case UserListRequest(user) =>
      sender ! UserListResponse(clients.except(user).map{ _.nick }.mkString(", "))

    case ChatMessage(from, message) =>
        val response = s"[$from] $message"
        val timestamp = getTimeStamp
        println(s"[$timestamp] ChatMessage from client: $response")

        clients foreach { _.reference ! PublicMessage(from, message, timestamp) }

    case PrivateChatMessage(from, to, message) =>
        clients.withNick(to) match {
          case Some(client: Client[ActorRef]) => client.reference ! PrivateMessage(from, message)
          case None => sender ! Notify(s"User $to offline, message not delivered")
        }
    case Logout =>
      clients.withRef(sender) match {
        case Some(client: Client[ActorRef]) => {
          println(s"Disconnecting ${client.nick}")
          clients.disconnect(sender)
          clients foreach { _.reference ! Notify(s"${client.nick} is now offline") }
        }
        case None => println("Unknown/stale client tried to disconnect, ignoring")
      }
    case _ =>
        println("something unexpected")
  }

  def getTimeStamp = {
    val hourFormat: SimpleDateFormat = new SimpleDateFormat("hh:mm:ss")
    val time = Calendar.getInstance().getTime()
    hourFormat.format(time)
  }
}
