package service

import cats.effect.IO
import domain.{CarsCount, CarsPerDay}
import fs2.Stream
import scala.annotation.tailrec

object TrafficDataService {

  def carsInTotal(s: Stream[IO, CarsCount]): IO[Int] =
    s.map(_.count).compile.fold(0)(_ + _)

  def carsByDate(s: Stream[IO, CarsCount]): IO[List[CarsPerDay]] =
    s.compile
      .toList
      .map(_.groupBy(_.dt.toLocalDate)
        .toList
        .map { case (dt, c) => CarsPerDay(dt, c.map(_.count).sum) }
      )

  def top3HalfHours(s: Stream[IO, CarsCount]): IO[List[CarsCount]] =
    s.compile.toList.map(_.sortBy(_.count)(Ordering[Int].reverse).take(3))


  private def findLeastCars(xs: List[CarsCount]): List[CarsCount] = {
    def sum(i: Int): Int = xs(i).count + xs(i + 1).count + xs(i + 2).count
    @tailrec
    def go(xs: List[CarsCount], i: Int, p: Int, acc: Int): List[CarsCount] = {
      if (i > xs.length - 4) {
        if(sum(i) < acc) xs(i) :: xs(i + 1) :: xs(i + 2) :: Nil else xs(p) :: xs(p + 1) :: xs(p + 2) :: Nil
      } else {
        val s = sum(i)
        if (s < acc) {
          go(xs, i + 1, i, s)
        } else {
          go(xs, i + 1, p, acc)
        }
      }
    }
    if (xs.length > 2) go(xs, 0, 0, Int.MaxValue) else Nil
  }

  def oneAndHalfHourWithLeastCars(s: Stream[IO, CarsCount]): IO[List[CarsCount]] =
    s.compile.toList.map(findLeastCars)

}
