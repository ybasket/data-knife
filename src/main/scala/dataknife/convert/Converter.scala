package dataknife.convert

import cats.effect.IO
import com.monovore.decline.Opts
import dataknife.format.Format
import fs2.Pipe

trait Converter[In <: Format: ValueOf, Out <: Format: ValueOf, Config] {
  val inputFormat: In = valueOf[In]
  val outputFormat: Out = valueOf[Out]
  def commandName: String = s"${inputFormat.name}2${outputFormat.name}"
  def commandHelp: String = s"Convert ${inputFormat.name.toUpperCase} to ${outputFormat.name.toUpperCase}"
  def configOpts: Opts[Config]
  def convert(config: Config): Pipe[IO, inputFormat.Data, outputFormat.Data]
  def convertByteToByte(config: Config, inputOptions: inputFormat.InputOptions, outputOptions: outputFormat.OutputOptions): Pipe[IO, Byte, Byte] = 
    _.through(inputFormat.read(inputOptions)).through(convert(config)).through(outputFormat.write(outputOptions))
}