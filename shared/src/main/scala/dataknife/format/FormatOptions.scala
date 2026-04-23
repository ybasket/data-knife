package dataknife.format

import com.monovore.decline.Opts

case class JsonInputOptions(jqQuery: Option[String] = None)

object JsonInputOptions {
  given Opts[JsonInputOptions] =
    Opts
      .option[String]("jq", "Apply a jq query to the JSON input")
      .orNone
      .map(JsonInputOptions(_))
}

case class CsvInputOptions(separator: Char = ',')

object CsvInputOptions {
  given Opts[CsvInputOptions] =
    Opts
      .option[Char]("csv-separator", "CSV separator character (default: ',')")
      .withDefault(',')
      .map(CsvInputOptions(_))
}

case class CborOutputOptions(diagnostic: Boolean = false)

object CborOutputOptions {
  given Opts[CborOutputOptions] =
    Opts
      .flag("diagnostic", "Output CBOR diagnostic notation instead of binary", "d")
      .orFalse
      .map(CborOutputOptions(_))
}

case class JsonOutputOptions(prettyPrint: Boolean = false)

object JsonOutputOptions {
  given Opts[JsonOutputOptions] =
    Opts
      .flag("pretty", "Pretty-print JSON output", "p")
      .orFalse
      .map(JsonOutputOptions(_))
}
