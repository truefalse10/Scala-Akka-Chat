package remote

class Clients[T] extends Traversable[Client[T]] {
  var clients: Map[String, Client[T]] = Map()

  def connect(nick: String, reference: T): Client[T] = {
    val client = new Client(nick, reference)
    clients = clients + (nick -> client)
    client
  }

  def withNick(nick: String) = clients.get(nick)
  def foreach[V](f: Client[T] => V) = clients.values.foreach(f)
}
