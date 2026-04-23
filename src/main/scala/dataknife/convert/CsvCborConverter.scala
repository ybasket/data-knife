package dataknife.convert

import cats.effect.IO
import com.monovore.decline.Opts
import dataknife.format.Format
import fs2.Pipe

object CsvCborConverter extends Converter[Format.Csv.type, Format.Cbor.type, Unit] {
  override val configOpts: Opts[Unit] = Opts.unit

  override def convert(config: Unit): Pipe[IO, Format.Csv.Data, Format.Cbor.Data] =
    _.through(CsvJsonConverter.convert(())).through(JsonCborConverter.convert(()))
}