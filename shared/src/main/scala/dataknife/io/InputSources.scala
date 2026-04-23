package dataknife.io

import com.monovore.decline.Opts
import fs2.io.file.Path

trait InputSources {
  import dataknife.cli.Arguments.given

  protected val fileInput: Opts[InputSource] =
    Opts
      .option[Path]("input", "Input file path", "i")
      .map(p => InputSource.FilePath(p))

}
