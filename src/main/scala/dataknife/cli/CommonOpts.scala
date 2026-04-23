package dataknife.cli

import cats.data.{NonEmptyList, Validated}
import com.monovore.decline.{Argument, Opts}
import dataknife.io.{InputSource, OutputTarget}
import fs2.io.file.Path
// import org.http4s.Uri

object CommonOpts {
  given Argument[Path] = Argument.from("path") { string =>
    Validated
      .catchNonFatal(Path(string))
      .leftMap(e => NonEmptyList.one(s"Invalid file path: ${e.getMessage}"))
  }

  private val fileInput: Opts[InputSource] =
    Opts
      .option[Path]("input", "Input file path", "i")
      .map(p => InputSource.FilePath(p))

  /*private val urlInput: Opts[InputSource] =
    Opts
      .option[String]("url", "Input URL", "u")
      .mapValidated(s =>
        Uri
          .fromString(s)
          .fold(
            e => Validated.invalidNel(s"Invalid URL: ${e.message}"),
            uri => Validated.validNel(InputSource.Url(uri)),
          )
      )*/

  val input: Opts[InputSource] =
    fileInput/*.orElse(urlInput)*/.withDefault(InputSource.StdIn)

  val output: Opts[OutputTarget] =
    Opts
      .option[Path]("output", "Output file path", "o")
      .map(p => OutputTarget.FilePath(p))
      .withDefault(OutputTarget.StdOut)
}