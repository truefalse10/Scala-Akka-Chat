package remote

import akka.actor._
import common._
import java.util.Calendar
import java.text.SimpleDateFormat

class Server extends Actor {

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
        val timestamp = getTimeStamp
        println(s"[$timestamp] ChatMessage from client: $response")

        clients.values foreach { _ ! PublicMessage(from, message, timestamp) }

    case PrivateChatMessage(from, to, message) =>
        clients get to match {
          case Some(recipient: ActorRef) => recipient ! PrivateMessage(from, message)
          case None => sender ! Notify(s"User $to offline, message not delivered")
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
