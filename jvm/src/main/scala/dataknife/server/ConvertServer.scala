package dataknife.server

import cats.effect.{ExitCode, IO}
import cats.syntax.all.*
import com.comcast.ip4s.*
import com.monovore.decline.{Command, Opts}
import dataknife.convert.*
import dataknife.format.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.`Content-Type` as ContentType

object ConvertServer {

  private case class ServerConfig(host: Host, port: Port)

  private val configOpts: Opts[ServerConfig] =
    (
      Opts
        .option[String]("host", "Host to bind to (default: 0.0.0.0)", "H")
        .mapValidated(h => Host.fromString(h).toValidNel(s"Invalid host: $h"))
        .withDefault(host"0.0.0.0"),
      Opts
        .option[Int]("port", "Port to bind to (default: 9474)")
        .mapValidated(p => Port.fromInt(p).toValidNel(s"Invalid port: $p"))
        .withDefault(port"9474")
    ).mapN(ServerConfig.apply)

  val command: Command[IO[ExitCode]] =
    Command("server", "Start an HTTP server exposing conversion routes") {
      configOpts.map { config =>
        EmberServerBuilder
          .default[IO]
          .withHost(config.host)
          .withPort(config.port)
          .withHttpApp(routes.orNotFound)
          .build
          .evalTap(_ => IO.println(s"Server started on ${config.host}:${config.port}"))
          .useForever
          .as(ExitCode.Success)
      }
    }

  private val octetStream = ContentType(MediaType.application.`octet-stream`)
  private val jsonContentType = ContentType(MediaType.application.json)
  private val textContentType = ContentType(MediaType.text.plain)

  private def csvInputOpts(req: Request[IO]): CsvInputOptions = {
    val sep = req.params.get("separator").flatMap(_.headOption).getOrElse(',')
    CsvInputOptions(sep)
  }

  private def jsonInputOpts(req: Request[IO]): JsonInputOptions =
    JsonInputOptions(req.params.get("jq"))

  private def jsonOutputOpts(req: Request[IO]): JsonOutputOptions =
    JsonOutputOptions(req.params.get("pretty").contains("true"))

  private def cborOutputOpts(req: Request[IO]): CborOutputOptions =
    CborOutputOptions(req.params.get("diagnostic").contains("true"))

  private def cborContentType(opts: CborOutputOptions): ContentType =
    if (opts.diagnostic) textContentType else octetStream

  private val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case req @ POST -> Root / "json" / "cbor" =>
      val outOpts = cborOutputOpts(req)
      val body = req.body.through(
        JsonCborConverter.convertByteToByte((), jsonInputOpts(req), outOpts)
      )
      Ok(body).map(_.withContentType(cborContentType(outOpts)))

    case req @ POST -> Root / "cbor" / "json" =>
      val outOpts = jsonOutputOpts(req)
      val body = req.body.through(
        CborJsonConverter.convertByteToByte((), (), outOpts)
      )
      Ok(body).map(_.withContentType(jsonContentType))

    case req @ POST -> Root / "csv" / "json" =>
      val outOpts = jsonOutputOpts(req)
      val body = req.body.through(
        CsvJsonConverter.convertByteToByte((), csvInputOpts(req), outOpts)
      )
      Ok(body).map(_.withContentType(jsonContentType))

    case req @ POST -> Root / "csv" / "cbor" =>
      val outOpts = cborOutputOpts(req)
      val body = req.body.through(
        CsvCborConverter.convertByteToByte((), csvInputOpts(req), outOpts)
      )
      Ok(body).map(_.withContentType(cborContentType(outOpts)))
  }
}
