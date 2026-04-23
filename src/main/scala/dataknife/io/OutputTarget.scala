package dataknife.io

import cats.effect.IO
import fs2.Pipe
import fs2.io.file.{Files, Path}

enum OutputTarget {
  case FilePath(path: Path)
  case StdOut
}

object OutputTarget {
  def write(target: OutputTarget): Pipe[IO, Byte, Nothing] = target match {
    case FilePath(path) => Files[IO].writeAll(path)
    case StdOut         => fs2.io.stdout[IO]
  }
}
