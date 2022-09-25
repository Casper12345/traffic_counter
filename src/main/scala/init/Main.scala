package init

import cats.effect.{ExitCode, IO, IOApp}
import parser.FileParser
import service.TrafficDataService
import util.{ErrorLogger, OutputFormatter, OutputPrinter}

object Main extends IOApp {

  private val parser = new FileParser(32)

  override def run(args: List[String]): IO[ExitCode] = {
    val parsedData = parser.parseData("files/data.tsv")
    val data = parsedData.collect{ case Right(d) => d}

    for {
      total <- TrafficDataService.carsInTotal(data)
      byDate <- TrafficDataService.carsByDate(data)
      top3 <- TrafficDataService.top3HalfHours(data)
      leastCars <- TrafficDataService.oneAndHalfHourWithLeastCars(data)
      _ <- OutputPrinter.printOutput(OutputFormatter.formatTotalCars(total))
      _ <- OutputPrinter.printOutput(OutputFormatter.formatCarsPerDay(byDate))
      _ <- OutputPrinter.printOutput(OutputFormatter.formatTop3HalfHours(top3))
      _ <- OutputPrinter.printOutput(OutputFormatter.oneAndHalfHourWithLeastCars(leastCars))
      _ <- parsedData.collect { case Left(e) => e }.compile.toList.flatMap(ErrorLogger.logErrorsWithWarning)
    } yield {
      ExitCode.Success
    }

  }

}

