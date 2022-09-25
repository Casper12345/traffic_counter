package util

import cats.effect.IO
import domain.{CarsCount, CarsPerDay}
import org.slf4j.LoggerFactory

object ErrorLogger {
  private val logger = LoggerFactory.getLogger(getClass)

  def logErrorsWithWarning(xs: List[Throwable]): IO[Unit] = IO {
      xs.foreach(e => logger.warn(e.toString))
    }

}

object OutputFormatter {

  def carsCountFormat(xs: List[CarsCount]): String =
    xs.map(c => s"${c.dt} ${c.count}").reduce((s1, s2) => s1 + "\n" + s2)

  def formatTotalCars(s: Int): String = s"cars in total: $s" + "\n"

  def formatCarsPerDay(xs: List[CarsPerDay]): String =
   "cars per day:\n" + xs.map(c => s"${c.date} ${c.count}").reduce((s1, s2) => s1 + "\n" + s2) + "\n"

  def formatTop3HalfHours(xs: List[CarsCount]): String =
    "top 3 half hours with most cars:\n" + carsCountFormat(xs) + "\n"

  def oneAndHalfHourWithLeastCars(xs: List[CarsCount]): String =
    "consecutive 1.5 hours with least cars:\n" + carsCountFormat(xs) + "\n"

}

object OutputPrinter {

  def printOutput(s: String): IO[Unit] = IO {println(s)}

}
