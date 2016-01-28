package common

case object Start
case class Message(msg: String)
case class ChatMessage(from: String, message: String)
case class UserListRequest(user: String)
case class UserListResponse(users: String)
case class Login(user: String)
