package dataknife.format

import cats.effect.IO
import fs2.Pipe
import fs2.data.text.utf8._

sealed trait Format(val name: String) {
  type Data
  type InputOptions
  type OutputOptions
  def read(opts: InputOptions): Pipe[IO, Byte, Data]
  def write(opts: OutputOptions): Pipe[IO, Data, Byte]
}

object Format {
  case object Json extends Format("json") {
    override type Data = fs2.data.json.Token
    override type InputOptions = Unit
    override type OutputOptions = JsonOutputOptions

    def read(opts: InputOptions): Pipe[IO, Byte, Data] = fs2.data.json.tokens[IO, Byte]
    def write(opts: OutputOptions): Pipe[IO, Data, Byte] = {
      val renderPipe: Pipe[IO, Data, String] =
        if (opts.prettyPrint) fs2.data.json.render.prettyPrint[IO]()
        else fs2.data.json.render.compact[IO]
      renderPipe andThen fs2.text.utf8.encode
    }
  }
  
  case object Cbor extends Format("cbor") {
    override type Data = fs2.data.cbor.low.CborItem
    override type InputOptions = Unit
    override type OutputOptions = Unit
    def read(opts: InputOptions): Pipe[IO, Byte, Data] = fs2.data.cbor.low.items[IO]
    def write(opts: OutputOptions): Pipe[IO, Data, Byte] = fs2.data.cbor.low.toBinary
  }
}
