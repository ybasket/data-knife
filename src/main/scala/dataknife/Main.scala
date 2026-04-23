package dataknife

import cats.effect.{ExitCode, IO}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import dataknife.cli.SubcommandBuilder
import dataknife.convert.*

object Main extends CommandIOApp(
  name = "data-knife",
  header = "Swiss-army knife for data format conversion",
  version = "0.1.0",
) {
  
  given Opts[Unit] = Opts.unit

  override def main: Opts[IO[ExitCode]] =
    List(
      SubcommandBuilder.fromConverter(JsonCborConverter),
      SubcommandBuilder.fromConverter(CborJsonConverter),
    ).reduceLeft(_ orElse _)
}
