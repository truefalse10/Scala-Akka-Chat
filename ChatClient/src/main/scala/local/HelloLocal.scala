package local

import akka.actor._
import Console._
import Stream._
import common._

import scala.util.Random

object Local extends App {

  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor")  // the local actor

  println("Welcome. Use /login <name> to register at the server")
  Stream.continually(Console.readLine(">>> ")).takeWhile( _ ne null) foreach { line =>
    if (line.startsWith("/login ")) {
      localActor ! Nickname(line.split(" ").last)
    } else {
      localActor ! LocalMessage(line);
    }
  }
}

case class LocalMessage(message: String)
case class Nickname(name: String)


class LocalActor extends Actor {
  var user = ""
  val remote = context.actorFor("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")

  def receive = {
    case LocalMessage(message) =>
      remote ! ChatMessage(user, message)

    case Nickname(name) =>
      if (name != null && !name.isEmpty) user = name
      remote ! Login(user)

    case ChatMessage(from, message) =>
      if (!user.equals(from)) output(s"<$from> $message")

    case _ =>
      output("something unexpected")
  }

  def output(message: String) = {
    println(s"\r$message")
    print(">>> ")
  }
}
