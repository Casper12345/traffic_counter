package service

import cats.effect.IO
import domain.{CarsCount, CarsPerDay}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import fs2.Stream
import cats.effect.unsafe.implicits.global
import java.time.{LocalDate, LocalDateTime}

class TrafficDataServiceTest extends AsyncFlatSpec with Matchers {

  "carsInTotal" should "should sum full amount for cars" in {

    val carsStream = Stream[IO, CarsCount](
      CarsCount(LocalDateTime.now, 1),
      CarsCount(LocalDateTime.now, 10),
      CarsCount(LocalDateTime.now, 19),
      CarsCount(LocalDateTime.now, 10),
    )

    TrafficDataService.carsInTotal(carsStream).unsafeToFuture.map { result =>
      result shouldBe 40
    }

  }

  it should "return 0 on an empty stream" in {
    val carsStream = Stream[IO, CarsCount]()
    TrafficDataService.carsInTotal(carsStream).unsafeToFuture.map { result =>
      result shouldBe 0
    }
  }

  "carsByDate" should "return sum of cars grouped by date" in {
    val carsStream = Stream[IO, CarsCount](
      CarsCount(LocalDateTime.parse("2021-12-01T09:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-01T09:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-02T09:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-02T09:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-03T09:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-03T09:00"), 1)
    )

    TrafficDataService.carsByDate(carsStream).unsafeToFuture.map { result =>
      result.sortBy(_.date) shouldEqual List(
        CarsPerDay(LocalDate.parse("2021-12-01"), 2),
        CarsPerDay(LocalDate.parse("2021-12-02"), 2),
        CarsPerDay(LocalDate.parse("2021-12-03"), 2)
      )
    }
  }

  it should "return an empty list on an empty input stream" in {
    val carsStream = Stream[IO, CarsCount]()

    TrafficDataService.carsByDate(carsStream).unsafeToFuture.map { result =>
      result shouldEqual Nil
    }
  }

  "top3HalfHours" should "return the three half hour periods with the most cars" in {
    val carsStream = Stream[IO, CarsCount](
      CarsCount(LocalDateTime.parse("2021-12-01T09:00"), 13),
      CarsCount(LocalDateTime.parse("2021-12-01T09:30"), 11),
      CarsCount(LocalDateTime.parse("2021-12-02T10:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-02T10:30"), 1),
      CarsCount(LocalDateTime.parse("2021-12-03T11:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-03T11:30"), 8)
    )

    TrafficDataService.top3HalfHours(carsStream).unsafeToFuture.map { result =>
      result shouldEqual List(
        CarsCount(LocalDateTime.parse("2021-12-01T09:00"), 13),
        CarsCount(LocalDateTime.parse("2021-12-01T09:30"), 11),
        CarsCount(LocalDateTime.parse("2021-12-03T11:30"), 8)
      )
    }
  }

  it should "return an empty list on an empty input stream" in {
    val carsStream = Stream[IO, CarsCount]()

    TrafficDataService.top3HalfHours(carsStream).unsafeToFuture.map { result =>
      result shouldEqual Nil
    }
  }

  "oneAndHalfHourWithLeastCars" should "return the consecutive one and half and hour with least cars" in {
    val carsStream = Stream[IO, CarsCount](
      CarsCount(LocalDateTime.parse("2021-12-01T20:00"), 13),
      CarsCount(LocalDateTime.parse("2021-12-01T20:30"), 11),
      CarsCount(LocalDateTime.parse("2021-12-01T21:00"), 0),
      CarsCount(LocalDateTime.parse("2021-12-01T21:30"), 1),
      CarsCount(LocalDateTime.parse("2021-12-01T22:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-01T22:30"), 21),
      CarsCount(LocalDateTime.parse("2021-12-01T23:00"), 0),
      CarsCount(LocalDateTime.parse("2021-12-01T23:30"), 8),
      CarsCount(LocalDateTime.parse("2021-12-02T00:00"), 3),
      CarsCount(LocalDateTime.parse("2021-12-02T00:30"), 19),
      CarsCount(LocalDateTime.parse("2021-12-02T01:00"), 8),
    )

    TrafficDataService.oneAndHalfHourWithLeastCars(carsStream).unsafeToFuture.map { result =>
      result shouldEqual List(
        CarsCount(LocalDateTime.parse("2021-12-01T21:00"), 0),
        CarsCount(LocalDateTime.parse("2021-12-01T21:30"), 1),
        CarsCount(LocalDateTime.parse("2021-12-01T22:00"), 1)
      )
    }

  }

  it should "check that last tree records in the stream are taken into consideration" in {
    val carsStream = Stream[IO, CarsCount](
      CarsCount(LocalDateTime.parse("2021-12-01T20:00"), 13),
      CarsCount(LocalDateTime.parse("2021-12-01T20:30"), 11),
      CarsCount(LocalDateTime.parse("2021-12-01T21:00"), 0),
      CarsCount(LocalDateTime.parse("2021-12-01T21:30"), 1),
      CarsCount(LocalDateTime.parse("2021-12-01T22:00"), 1),
      CarsCount(LocalDateTime.parse("2021-12-01T22:30"), 21),
      CarsCount(LocalDateTime.parse("2021-12-01T23:00"), 0),
      CarsCount(LocalDateTime.parse("2021-12-01T23:30"), 8),
      CarsCount(LocalDateTime.parse("2021-12-02T00:00"), 0),
      CarsCount(LocalDateTime.parse("2021-12-02T00:30"), 0),
      CarsCount(LocalDateTime.parse("2021-12-02T01:00"), 0),
    )

    TrafficDataService.oneAndHalfHourWithLeastCars(carsStream).unsafeToFuture.map { result =>
      result shouldEqual List(
        CarsCount(LocalDateTime.parse("2021-12-02T00:00"), 0),
        CarsCount(LocalDateTime.parse("2021-12-02T00:30"), 0),
        CarsCount(LocalDateTime.parse("2021-12-02T01:00"), 0),
      )
    }

  }

  it should "return an empty list on an empty input stream" in {
    val carsStream = Stream[IO, CarsCount]()

    TrafficDataService.oneAndHalfHourWithLeastCars(carsStream).unsafeToFuture.map { result =>
      result shouldEqual Nil
    }
  }

}
