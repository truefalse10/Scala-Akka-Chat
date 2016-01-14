import akka.actor._

class HelloActor(myName: String) extends Actor {
  def receive = {
    case "hello" => println("[%s] hello".format(myName))
    case _       => println("[%s] huh?".format(myName))
  }
}

object Main extends App {
  val system = ActorSystem("ChatServer")
  // default Actor constructor
  val firstActor = system.actorOf(Props(new HelloActor("Sebi")), name = "firstactor")

  val username = "Server"
  println("Welcome to the akka-chat, your name is [%s]".format(username))
  firstActor ! "hello"
  firstActor ! "buenos dias"
}
