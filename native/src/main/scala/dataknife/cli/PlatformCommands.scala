package dataknife.cli

import cats.effect.{ExitCode, IO}
import com.monovore.decline.Command

trait PlatformCommands {
  val platformCommands: List[Command[IO[ExitCode]]] = Nil
}
