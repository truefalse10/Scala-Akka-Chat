import akka.actor._
import akka.remote._

sealed trait Event
// SCALA: Case classes sind immutable and easy to pattern match
case class Login(user: String) extends Event
case class Logout(user: String) extends Event
case class GetChatLog(from: String) extends Event
case class ChatLog(log: List[String]) extends Event
case class ChatMessage(from: String, message: String) extends Event

class ChatClient(val name: String) {
  val chat = Actor.remote.actorFor("chat:service", "localhost", 2552)

  // define client functions
  def login = chat ! Login(name)
  def logout = chat ! Logout(name)
  def post(message: String) = chat ! ChatMessage(name, "[" + name + "] " + message)
  def chatLog = (chat ? GetChatLog(name)).as[ChatLog].getOrElse(throw new Exception("could not get chat log from Server"))

}

class Session(user: String, storage: ActorRef) extends Actor {
  private val loginTime = System.currentTimeMillis
//  private var userLog = List[String] = Nil

  EventHandler.info(this, "new sessions for user [%s] has been created at [%s]".format(user, loginTime))

  def receive = {
    case msg @ ChatMessage(from, message) =>
      println(message)
      // userLog ::= message
      // storage ! msg

    case msg @ GetChatLog(_) =>
      storage forward msg
  }

}


class HelloActor(myName: String) extends Actor {
  def receive = {
    case "hello" => println("[%s] hello".format(myName))
    case _       => println("[%s] huh?".format(myName))
  }
}

object Main extends App {
  val system = ActorSystem("ChatServer")



  val client = new ChatClient("Client")
  val server = system.actorOf(Props(new Session(client), name = "session"))

  client.post("testmessage_blabla")


  // default Actor constructor
  //val firstActor = system.actorOf(Props(new HelloActor("Sebi")), name = "firstactor")

}
