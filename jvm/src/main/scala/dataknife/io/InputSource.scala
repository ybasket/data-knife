package dataknife.io

import cats.data.{NonEmptyList, Validated}
import cats.effect.IO
import com.monovore.decline.Opts
import fs2.Stream
import fs2.io.file.{Files, Path}
import org.http4s.{Request, Uri}
import org.http4s.ember.client.EmberClientBuilder

enum InputSource {
  case FilePath(path: Path)
  case Url(uri: Uri)
  case StdIn
}

object InputSource extends InputSources {

  private val urlInput: Opts[InputSource] =
    Opts
      .option[String]("url", "Input URL", "u")
      .mapValidated(s =>
        Uri
          .fromString(s)
          .fold(
            e => Validated.invalidNel(s"Invalid URL: ${e.message}"),
            uri => Validated.validNel(InputSource.Url(uri))
          )
      )

  given opts: Opts[InputSource] =
    fileInput.orElse(urlInput).withDefault(InputSource.StdIn)

  def bytes(source: InputSource): Stream[IO, Byte] = source match {
    case FilePath(path) => Files[IO].readAll(path)
    case StdIn          => fs2.io.stdin[IO](4096)
    case Url(uri)       =>
      Stream
        .resource(EmberClientBuilder.default[IO].build)
        .flatMap(client => client.stream(Request[IO](uri = uri)).flatMap(_.body))
  }
}
