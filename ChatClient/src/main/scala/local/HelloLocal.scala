package local

import akka.actor._
import Console._
import Stream._
import common._

object Local extends App {

  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor")  // the local actor
  localActor ! "STARTCLIENT"

  Stream.continually(Console.readLine(">>> ")).takeWhile( _ ne null) foreach { line =>
    localActor ! LocalMessage(line);
  }
}

case class LocalMessage(message: String)


class LocalActor extends Actor {

  val user = "Sebastian"
  // create the remote actor (Akka 2.1 syntax)
  val remote = context.actorFor("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")

  def receive = {
    case "STARTCLIENT" =>
        remote ! Login(user)

    case LocalMessage(message) =>
        remote ! ChatMessage(user, message)

    case ChatMessage(from, message) =>
        println(s"server: $message")

    case _ =>
        println("something unexpected")
  }
}
