package dataknife.cli

import cats.data.{NonEmptyList, Validated}
import com.monovore.decline.Argument
import fs2.io.file.Path

object CommonOpts {
  given Argument[Path] = Argument.from("path") { string =>
    Validated
      .catchNonFatal(Path(string))
      .leftMap(e => NonEmptyList.one(s"Invalid file path: ${e.getMessage}"))
  }
}