package dataknife.io

import cats.effect.IO
import com.monovore.decline.Opts
import fs2.Pipe
import fs2.io.file.{Files, Path}

enum OutputTarget {
  case FilePath(path: Path)
  case StdOut
}

object OutputTarget {
  import dataknife.cli.Arguments.given

  given opts: Opts[OutputTarget] =
    Opts
      .option[Path]("output", "Output file path", "o")
      .map(p => OutputTarget.FilePath(p))
      .withDefault(OutputTarget.StdOut)

  def write(target: OutputTarget): Pipe[IO, Byte, Nothing] = target match {
    case FilePath(path) => Files[IO].writeAll(path)
    case StdOut         => fs2.io.stdout[IO]
  }
}
