package dataknife.io

import cats.effect.IO
import com.monovore.decline.Opts
import fs2.Stream
import fs2.io.file.{Files, Path}

enum InputSource {
  case FilePath(path: Path)
  case StdIn
}

object InputSource extends InputSources {
  given opts: Opts[InputSource] = fileInput.withDefault(InputSource.StdIn)

  def bytes(source: InputSource): Stream[IO, Byte] = source match {
    case FilePath(path) => Files[IO].readAll(path)
    case StdIn          => fs2.io.stdin[IO](4096)
  }
}
