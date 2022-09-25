package domain

import java.time.{LocalDate, LocalDateTime}

case class CarsCount(dt: LocalDateTime, count: Int)
case class CarsPerDay(date: LocalDate, count: Int)
case class ParserFormatException(message: String) extends Exception(message)
