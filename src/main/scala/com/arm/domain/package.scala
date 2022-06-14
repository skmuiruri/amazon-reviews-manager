package com.arm

import com.arm.payload.{RawReview, RawRequestParams}
import zio._
import zio.prelude.Validation

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, ZoneId}
import scala.util.{Failure, Success, Try}


package object domain {
  case class Review(asin: String, helpful: (Int, Int), overall: Double, reviewTime: LocalDate)

  object Review {
    def from(r: RawReview): ZIO[Any, Throwable, Review] = {
      Try(LocalDate.ofInstant(Instant.ofEpochSecond(r.unixReviewTime), ZoneId.of("UTC"))) match {
        case Failure(e) => ZIO.fail(new IllegalArgumentException(s"${e.getCause} while converting ${r.unixReviewTime} to LocalDate. ${e.getMessage}"))
        case Success(d) => ZIO.succeed(Review(r.asin, r.helpful, r.overall, d))
      }
    }
  }

  case class RequestParams(start: LocalDate, end: LocalDate, limit: Int, minNumberReviews: Int)

  object RequestParams {
    private val dateFormat = "dd.MM.yyyy"

    private def validateDate(fieldName: String, input: String): Validation[String, LocalDate] =
      Try(LocalDate.parse(input, DateTimeFormatter.ofPattern(dateFormat))) match {
        case Failure(e) => Validation.fail(s"${e.getCause} while parsing $fieldName with value $input to a date of format $dateFormat")
        case Success(v) => Validation.succeed(v)
      }

    def from(r: RawRequestParams): IO[Throwable, RequestParams] = {
      Validation.validateWith(validateDate("start", r.start), validateDate("end", r.end))(
        (start, end) => RequestParams(start, end, r.limit, r.minNumberReviews)
      ).mapError(s => new Exception(s)).toZIO
    }
  }

}
