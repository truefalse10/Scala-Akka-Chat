package local

import akka.actor._
import common._

object Local extends App {

  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor")  // the local actor
  localActor ! "STARTCLIENT"                                                     // start the action
  localActor ! ChatMessage("client", "mymessage with some chars")

}

class LocalActor extends Actor {

  // create the remote actor (Akka 2.1 syntax)
  val remote = context.actorFor("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")

  def receive = {
    case "STARTCLIENT" =>
        remote ! "LOGIN"

    case ChatMessage(from, message) =>
        println("message from server: " + message)

    case _ =>
        println("something unexpected")
  }
}
