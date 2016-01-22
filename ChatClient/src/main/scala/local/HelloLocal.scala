package local

import akka.actor._
import Console._
import Stream._
import common._

import scala.util.Random

object Local extends App {

  val nickname = args(0)

  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor")  // the local actor
  localActor ! Nickname(nickname)

  Stream.continually(Console.readLine(">>> ")).takeWhile( _ ne null) foreach { line =>
    localActor ! LocalMessage(line);
  }
}

case class LocalMessage(message: String)

case class Nickname(message: String)


class LocalActor extends Actor {

  var user = ""
  // create the remote actor (Akka 2.1 syntax)
  val remote = context.actorFor("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")

  def receive = {
    case LocalMessage(message) =>
        remote ! ChatMessage(user, message)

    case Nickname(message) =>
      user = if (message != null) message else Random.nextString(7)
      remote ! Login(user)

    case ChatMessage(from, message) =>
        println(s"server: $message")

    case _ =>
        println("something unexpected")
  }

  def setNick(nickname: String) = {

  }
}
