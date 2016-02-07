package common

case object Start
case class ChatMessage(from: String, message: String)
case class PublicMessage(from: String, message: String)
case class PrivateMessage(from: String, message: String)
case class Notify(message: String)
case class PrivateChatMessage(from: String, to: String, message: String)
case class UserListRequest(user: String)
case class UserListResponse(users: String)
case class ValidateName(user: String)
case class Login(user: String)
