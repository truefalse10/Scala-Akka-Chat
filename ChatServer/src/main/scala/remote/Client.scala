package remote

class Client[T](val nick: String, val reference: T) {
  def withReference(f: (T => Unit)) = f(reference)
}
