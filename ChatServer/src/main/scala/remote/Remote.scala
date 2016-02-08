package remote

import akka.actor._
import common._

object Remote extends App  {
  val system = ActorSystem("HelloRemoteSystem")
  val remoteActor = system.actorOf(Props[Server], name = "Server")
  remoteActor ! "STARTSERVER"
}
