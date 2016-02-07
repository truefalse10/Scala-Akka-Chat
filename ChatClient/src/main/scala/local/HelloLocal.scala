package local

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import Console._
import common._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object Local extends App {

  val greeting: String = "Use /login <name> to register at the server."

  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor")  // the local actor

  println(greeting)
  Stream.continually(Console.readLine(">>> ")).takeWhile( _ ne null) foreach { line =>
    if (line.startsWith("/login ")) {
      localActor ! Nickname(line.split(" ").last)
    } else {
      localActor ! LocalMessage(line)
    }
  }
}

case class LocalMessage(message: String)
case class Nickname(name: String)

class LocalActor extends Actor {
  var user = ""
  val remote = context.actorFor("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")

  val nameExists: String = "This name already exists. Please choose another one."
  val nameInvalid: String = "Please choose a valid name!"

  def receive = {
    case LocalMessage(message) if message equals "/ls" =>
      remote ! UserListRequest(user)

    case LocalMessage(message) if message startsWith "@" =>
      val index = message indexOf " "
      remote ! PrivateChatMessage(user, message substring(1, index), message substring index+1 )

    case LocalMessage(message) =>
      remote ! ChatMessage(user, message)

    case PublicMessage(from, message) =>
      if (!user.equals(from)) output(s"<$from> $message")

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
