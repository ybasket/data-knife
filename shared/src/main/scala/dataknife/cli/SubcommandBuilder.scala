package dataknife.cli

import cats.effect.{ExitCode, IO}
import cats.effect.std.Console
import cats.syntax.all.*
import com.monovore.decline.{Command, Opts}
import dataknife.convert.Converter
import dataknife.format.Format
import dataknife.io.{InputSource, OutputTarget}

object SubcommandBuilder {
  def fromConverter[In <: Format, Out <: Format, C](
      conv: Converter[In, Out, C]
  )(using io: Opts[conv.inputFormat.InputOptions], oo: Opts[conv.outputFormat.OutputOptions]): Command[IO[ExitCode]] =
    Command(conv.commandName, conv.commandHelp) {
      (InputSource.opts, io, OutputTarget.opts, oo, conv.configOpts).mapN {
        (input, inputOptions, output, outputOptions, config) =>
          InputSource
            .bytes(input)
            .through(conv.convertByteToByte(config, inputOptions, outputOptions))
            .through(OutputTarget.write(output))
            .compile
            .drain
            .as(ExitCode.Success)
            .handleErrorWith { e =>
              Console[IO].errorln(s"Error: ${e.getMessage}") >>
                Console[IO].printStackTrace(e).as(ExitCode.Error)
            }
      }
    }
}
