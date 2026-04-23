package dataknife

import cats.effect.{ExitCode, IO}
import cats.syntax.all.*
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import dataknife.cli.{PlatformCommands, SubcommandBuilder}
import dataknife.convert.*

object Main
    extends CommandIOApp(
      name = "data-knife",
      header = "Swiss-army knife for data format conversion",
      version = "0.1.0"
    )
    with PlatformCommands {

  // For formats/conversions that have no specific options
  given Opts[Unit] = Opts.unit

  override def main: Opts[IO[ExitCode]] =
    Opts.subcommands(
      SubcommandBuilder.fromConverter(JsonCborConverter),
      SubcommandBuilder.fromConverter(CborJsonConverter) ::
        SubcommandBuilder.fromConverter(CsvJsonConverter) ::
        SubcommandBuilder.fromConverter(CsvCborConverter) ::
        platformCommands*
    )
}
