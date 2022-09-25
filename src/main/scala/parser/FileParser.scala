package parser

import cats.effect.IO
import domain.{CarsCount, ParserFormatException}
import fs2.{Pipe, Stream, text}
import fs2.io.file.{Files, Path}
import java.time.LocalDateTime
import scala.util.Try

class FileParser(maxConcurrent: Int) {

  private def parser[F[_]]: Pipe[F, Byte, List[String]] =
    _.through(text.utf8.decode)
      .through(text.lines)
      .map(_.split(' ').toList)


  private[parser] def parseCarsData(xs: List[String]): Either[Throwable, CarsCount] = xs match {
    case dt :: count :: Nil =>
      for {
        dt <- Try(LocalDateTime.parse(dt)).toEither
        count <- Try(count.toInt).toEither
      } yield {
         CarsCount(dt, count)
      }
    case _ => Left(ParserFormatException("Unknown line format"))
  }

  def parseData(path: String): Stream[IO, Either[Throwable, CarsCount]] =
    Files[IO].readAll(Path(path)).through(parser).mapAsync(maxConcurrent)( xs =>
      IO.pure(parseCarsData(xs))
    )

}
