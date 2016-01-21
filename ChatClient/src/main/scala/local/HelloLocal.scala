package local

import akka.actor._
import Console._
import Stream._
import common._

object Local extends App {

  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor")  // the local actor
  localActor ! ChatMessage("client", "mymessage with some chars")

  Stream.continually(Console.readLine(">>> ")).takeWhile( _ ne null) foreach { line =>
    localActor ! LocalMessage(line);
  }
}

case class LocalMessage(message: String)


class LocalActor extends Actor {

  // create the remote actor (Akka 2.1 syntax)
  val remote = context.actorFor("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")

  def receive = {
    case LocalMessage(message) =>
        remote ! ChatMessage("client", message)

    case ChatMessage(from, message) =>
        println(s"server: $message")

    case _ =>
        println("something unexpected")
  }
}
