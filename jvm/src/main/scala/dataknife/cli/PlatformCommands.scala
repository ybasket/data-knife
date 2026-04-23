package dataknife.cli

import cats.effect.{ExitCode, IO}
import com.monovore.decline.Command
import dataknife.server.ConvertServer

trait PlatformCommands {
  val platformCommands: List[Command[IO[ExitCode]]] = List(ConvertServer.command)
}
