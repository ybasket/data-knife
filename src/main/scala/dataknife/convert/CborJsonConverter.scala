package dataknife.convert

import cats.effect.IO
import com.monovore.decline.Opts
import dataknife.format.Format
import fs2.Pipe
import fs2.data.cbor.json.decodeItems
import fs2.data.cbor.low.CborItem
import fs2.data.json.Token

object CborJsonConverter extends Converter[Format.Cbor.type, Format.Json.type, Unit] {
  override val configOpts: Opts[Unit] = Opts.unit

  def convert(config: Unit): Pipe[IO, CborItem, Token] = _.through(decodeItems[IO])
}
