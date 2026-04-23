package dataknife.io

import cats.effect.IO
import fs2.Stream
import fs2.io.file.{Files, Path}
import org.http4s.Uri
import org.http4s.Request
import org.http4s.ember.client.EmberClientBuilder

enum InputSource {
  case FilePath(path: Path)
  case Url(uri: Uri)
  case StdIn
}

object InputSource {
  def bytes(source: InputSource): Stream[IO, Byte] = source match {
    case FilePath(path) => Files[IO].readAll(path)
    case StdIn          => fs2.io.stdin[IO](4096)
    case Url(uri) =>
      Stream
        .resource(EmberClientBuilder.default[IO].build)
        .flatMap(client => client.stream(Request[IO](uri = uri)).flatMap(_.body))
  }
}