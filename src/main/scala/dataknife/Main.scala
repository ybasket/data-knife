package dataknife

import cats.effect.{ExitCode, IO}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import dataknife.cli.SubcommandBuilder
import dataknife.convert.*
import dataknife.server.ConvertServer

object Main
    extends CommandIOApp(
      name = "data-knife",
      header = "Swiss-army knife for data format conversion",
      version = "0.1.0"
    ) {

  // For formats/conversions that have no specific options
  given Opts[Unit] = Opts.unit

  override def main: Opts[IO[ExitCode]] =
    Opts.subcommands(
      SubcommandBuilder.fromConverter(JsonCborConverter),
      SubcommandBuilder.fromConverter(CborJsonConverter),
      SubcommandBuilder.fromConverter(CsvJsonConverter),
      SubcommandBuilder.fromConverter(CsvCborConverter),
      ConvertServer.command
    )
}