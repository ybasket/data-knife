package dataknife.format

import com.monovore.decline.Opts

case class JsonOutputOptions(prettyPrint: Boolean = false)

object JsonOutputOptions {
  given Opts[JsonOutputOptions] =
    Opts
      .flag("pretty", "Pretty-print JSON output", "p")
      .orFalse
      .map(JsonOutputOptions(_))
}
