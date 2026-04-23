package dataknife.convert

import cats.effect.IO
import com.monovore.decline.Opts
import dataknife.format.Format
import fs2.Pipe
import fs2.data.json.cbor.encodeItems

object JsonCborConverter extends Converter[Format.Json.type, Format.Cbor.type, Unit] {
  val configOpts = Opts.unit

  override def convert(config: Unit): Pipe[IO, Format.Json.Data, Format.Cbor.Data] =
    _.through(encodeItems)
}