package dataknife.convert

import cats.effect.IO
import cats.syntax.all.*
import com.monovore.decline.Opts
import dataknife.format.Format
import fs2.data.csv.CsvRow
import fs2.data.json.Token
import fs2.{Pipe, Stream}

import scala.util.Try

object CsvJsonConverter extends Converter[Format.Csv.type, Format.Json.type, Unit] {

  override val configOpts: Opts[Unit] = Opts.unit

  def convert(config: Unit): Pipe[IO, CsvRow[String], Token] = rows => {
    Stream.emit(Token.StartArray) ++ rows.flatMap(rowToTokens) ++ Stream.emit(Token.EndArray)
  }

  private def rowToTokens(row: CsvRow[String]): Stream[IO, Token] = {
    Stream.emit(Token.StartObject) ++ Stream.foldable(row.headers.get).flatMap { header =>
      Stream(Token.Key(header), cellToToken(row(header).orEmpty))
    } ++ Stream.emit(Token.EndObject)
  }

  private def cellToToken(value: String): Token =
    value match {
      case ""                                => Token.NullValue
      case "true"                            => Token.TrueValue
      case "false"                           => Token.FalseValue
      case v if Try(BigDecimal(v)).isSuccess => Token.NumberValue(v)
      case v                                 => Token.StringValue(v)
    }
}
