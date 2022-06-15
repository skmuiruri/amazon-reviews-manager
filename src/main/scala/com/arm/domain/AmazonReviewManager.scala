package com.arm.domain

import com.arm.payload.{RawReview, ResponseData}
import zio.blocking.Blocking
import zio.json.DecoderOps
import zio.stream.{ZSink, ZStream, ZTransducer}
import zio.{Has, ZIO, ZLayer}

import java.nio.file.Paths

object AmazonReviewManager {

  type AmazonReviewManagerService = Has[AmazonReviewManager.Service]

  final case class ProductId(asin: String)

  final case class AggregatedReviewData(reviewCount: Int, sumReviewRatings: Double) {
    def averageRating: Double = sumReviewRatings / reviewCount.toDouble

    def addRating(rating: Double): AggregatedReviewData = copy(reviewCount = reviewCount + 1, sumReviewRatings = sumReviewRatings + rating)
  }

  trait Service {
    def findTopProducts(filePath: String, params: RequestParams): ZIO[Blocking, Throwable, Vector[ResponseData]]
  }


  val live: ZLayer[Any, Throwable, AmazonReviewManagerService] = ZIO.succeed(new Service {
    override def findTopProducts(filePath: String, params: RequestParams): ZIO[Blocking, Throwable, Vector[ResponseData]] = {
      val aggregatedMap: ZIO[Blocking, Throwable, Map[String, AggregatedReviewData]] = ZStream
        .fromFile(Paths.get(filePath))
        .transduce(ZTransducer.utf8Decode >>> ZTransducer.splitLines)
        .map(_.fromJson[RawReview].getOrElse(throw new IllegalArgumentException("invalid input")))
        .mapM(Review.from)
        .filter(r => r.reviewTime.isAfter(params.start) && r.reviewTime.isBefore(params.end))
        .run(ZSink.foldLeft(Map.empty[String, AggregatedReviewData]) {
          case (map, review) =>
            map + (review.asin -> (
              map.get(review.asin) match {
                case None => AggregatedReviewData(1, review.overall)
                case Some(d) => d.addRating(review.overall)
              }))
        })
      aggregatedMap
        .map(
          _.filter(_._2.reviewCount >= params.limit).toVector.sortWith(_._2.averageRating > _._2.averageRating).take(params.limit).map {
            case (asin, data) => ResponseData(asin, data.averageRating)
          }
        )
    }
  }).toLayer

  def findTopProducts(filePath: String, params: RequestParams): ZIO[Has[AmazonReviewManager.Service] with Has[Blocking.Service], Throwable, Vector[ResponseData]] =
    ZIO.accessM(service => service.get.findTopProducts(filePath, params))
}
