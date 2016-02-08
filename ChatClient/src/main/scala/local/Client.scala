package local

import Console._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import common._
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

case class LocalMessage(message: String)
case class Nickname(name: String)

class Client extends Actor {
  var user = ""
  var isLoggedIn = false;
  val remote = context.actorFor("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/Server")

  val nameExists: String = "This name already exists. Please choose another one."
  val nameInvalid: String = "Please choose a valid name!"

  def receive = {
    case LocalMessage(message) if message equals "/ls" =>
      remote ! UserListRequest(user)

    case LocalMessage(message) if message startsWith "@" =>
      val index = message indexOf " "
      remote ! PrivateChatMessage(user, message substring(1, index), message substring index+1 )

    case LocalMessage(message) =>
      if (isLoggedIn) {
        remote ! ChatMessage(user, message)
      }
      else {
        output("You have to login before you can start messaging. Use /login <name> to register at the server.")
      }


    case PublicMessage(from, message, timestamp) =>
      if (!user.equals(from)) output(s"<$from $timestamp> $message")

    case PrivateMessage(from, message) =>
      if (!user.equals(from)) output(s"<$from> <private> $message")

    case UserListResponse(users) =>
      output(s"Users online: $users")

    case Notify(message) =>
      output(s"Server notification: $message")

    case Nickname(name) =>
      implicit val timeout = Timeout(5 seconds)

      val future = remote ? ValidateName(name)
      Await.result(future, timeout.duration).asInstanceOf[String] match {
        case "Ok" =>
          remote ! Login(name)
          user = name
          isLoggedIn = true
          output(s"Welcome, $user")
        case "Invalid" => output(nameInvalid)
        case "Dupe" => output(nameExists)
        case _ => throw new IllegalStateException("Unknown server response")
      }

    case _ =>
      output("something unexpected")
  }

  def output(message: String) = {
    println(s"\r$message")
    print(">>> ")
  }
}
