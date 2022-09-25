package parser

import org.scalatest.flatspec.AsyncFlatSpec
import cats.effect.unsafe.implicits.global
import domain.{CarsCount, ParserFormatException}
import org.scalatest.matchers.should.Matchers
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class FileParserTest extends AsyncFlatSpec with Matchers {

  def fixture = new {
    val fileParser = new FileParser(100)

  }

  "parseData" should "parse data correctly" in {
    val f = fixture

    val testData = Seq(
      CarsCount(LocalDateTime.parse("2021-12-01T05:00"), 45),
      CarsCount(LocalDateTime.parse("2021-12-01T05:30"), 7),
      CarsCount(LocalDateTime.parse("2021-12-01T06:00"), 25),
      CarsCount(LocalDateTime.parse("2021-12-01T06:30"), 32),
      CarsCount(LocalDateTime.parse("2021-12-01T07:00"), 36),
      CarsCount(LocalDateTime.parse("2021-12-01T07:30"), 29),
      CarsCount(LocalDateTime.parse("2021-12-01T08:00"), 18),
      CarsCount(LocalDateTime.parse("2021-12-01T08:30"), 34),
      CarsCount(LocalDateTime.parse("2021-12-01T09:00"), 23),
      CarsCount(LocalDateTime.parse("2021-12-01T09:30"), 3)
    )

    f.fileParser.parseData("src/test/resources/files/correctly_formed_data.tsv")
      .compile.toList.unsafeToFuture().map { result =>
      val data = result.collect { case Right(v) => v}
      val errors = result.collect { case Left(e) => e}

      data.length shouldBe 10
      data shouldEqual testData
      errors.length shouldBe 0
    }
  }

  it should "parse errors" in {
    val f = fixture

    val testData = Seq(
      CarsCount(LocalDateTime.parse("2021-12-01T05:30"), 7),
      CarsCount(LocalDateTime.parse("2021-12-01T06:00"), 25),
      CarsCount(LocalDateTime.parse("2021-12-01T06:30"), 32),
      CarsCount(LocalDateTime.parse("2021-12-01T07:00"), 36),
      CarsCount(LocalDateTime.parse("2021-12-01T07:30"), 29),
      CarsCount(LocalDateTime.parse("2021-12-01T08:00"), 18),
      CarsCount(LocalDateTime.parse("2021-12-01T08:30"), 34),
      CarsCount(LocalDateTime.parse("2021-12-01T09:30"), 3)
    )

    f.fileParser.parseData("src/test/resources/files/malformed_data.tsv")
      .compile.toList.unsafeToFuture().map { result =>
      val data = result.collect { case Right(v) => v }
      val errors = result.collect { case Left(e) => e }

      data.length shouldBe 8
      data shouldEqual testData
      errors.length shouldBe 3
      errors.head match {
        case _: DateTimeParseException => succeed
        case x => fail(s"an incorrect exception was raised: $x")
      }
      errors(1) match {
        case _: NumberFormatException => succeed
        case x => fail(s"an incorrect exception was raised: $x")
      }
      errors(2) match {
        case _: ParserFormatException => succeed
        case x => fail (s"an incorrect exception was raised: $x")
      }
    }
  }

}
