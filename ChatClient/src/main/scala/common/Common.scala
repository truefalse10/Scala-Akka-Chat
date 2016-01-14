package common

case object Start
case class Message(msg: String)
case class ChatMessage(from: String, message: String)
