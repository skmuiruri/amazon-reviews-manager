package com.arm

import com.arm.domain.AmazonReviewManager
import com.arm.payload.ResponseData
import zio._
import zio.blocking.Blocking
import zio.test.Assertion._
import zio.test._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AmazonReviewManagerSpec extends DefaultRunnableSpec {
  private def toDate(date: String) = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))

  val dependencies = Blocking.live ++ AmazonReviewManager.live

  override def spec = suite("AmazonReviewManagerSpec")(
    testM("Return the correct top product ratings") {
      val path = "src/test/resources/amazon-test-reviews.json"
      val params = domain.RequestParams(toDate("01.01.2010"), toDate("31.12.2020"), 2, 2)
      val data: ZIO[Any, Throwable, Vector[payload.ResponseData]] = AmazonReviewManager.findTopProducts(path, params).provideLayer(dependencies)
      data.map(assert(_)(equalTo(Vector(ResponseData("B000JQ0JNS", 4.5), ResponseData("B000NI7RW8", 3.6666666666666665)))))
    }
  )
}
