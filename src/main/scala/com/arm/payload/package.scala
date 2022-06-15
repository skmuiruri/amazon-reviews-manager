package com.arm

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder, jsonField}

package object payload {

  case class RawRequestParams(start: String, end: String, limit: Int, @jsonField("min_number_reviews") minNumberReviews: Int)
  object RawRequestParams {
    implicit val decoder: JsonDecoder[RawRequestParams] = DeriveJsonDecoder.gen[RawRequestParams]
    implicit val encoder: JsonEncoder[RawRequestParams] = DeriveJsonEncoder.gen[RawRequestParams]
  }

  case class RawReview(asin: String, helpful: (Int, Int), overall: Double, unixReviewTime: Long)
  object RawReview {
    implicit val decoder: JsonDecoder[RawReview] = DeriveJsonDecoder.gen[RawReview]
  }

  case class ResponseData(asin: String, average_rating: Double)
  object ResponseData {
    implicit val encoder: JsonEncoder[ResponseData] = DeriveJsonEncoder.gen[ResponseData]
  }

}
