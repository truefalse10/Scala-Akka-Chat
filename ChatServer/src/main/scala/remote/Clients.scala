package remote

class Clients[T] extends Traversable[Client[T]] {
  var clients: Map[String, Client[T]] = Map()

  def connect(nick: String, reference: T): Client[T] = {
    val client = new Client(nick, reference)
    clients = clients + (nick -> client)
    client
  }
  def disconnect(reference: T) = {
    clients = clients.filter{_._2.reference != reference}
  }
  def connected(nick: String) = clients contains nick

  def withNick(nick: String) = clients.get(nick)
  def withRef(reference: T) = find{_.reference == reference}
  def except(nick: String) = filter{_.nick == nick}
  def foreach[V](f: Client[T] => V) = clients.values.foreach(f)

  def nicks = map { _.nick }
}
