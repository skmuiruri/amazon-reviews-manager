package com.arm.server

import com.arm.config.CoreConfigs
import com.arm.domain.{AmazonReviewManager, RequestParams}
import zhttp.http._
import zhttp.service
import zhttp.service.{EventLoopGroup, Server, ServerChannelFactory}
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.system.System
import zio.json._
import com.arm.payload._
import com.arm.payload.ResponseData
import com.arm.server.ReviewManagerServer.ToErrChannel

class ReviewManagerServer(configs: CoreConfigs, clock: Clock.Service, console: Console.Service, system: System.Service,
                          random: Random.Service, blocking: Blocking.Service, manager: AmazonReviewManager.Service) {
  private val PORT = 8080
  private val threadCount = 4

  private def expiredDocApp: HttpApp[Any, Throwable] = Http.collectZIO {
    case Method.GET -> !! / "test" => ZIO.succeed(Response.status(Status.OK))
    case req@Method.POST -> !! / "amazon" / "best-rated" =>
      (for {
        input <- req.getBodyAsString.map(_.fromJson[RawRequestParams]).toErrChannel
        params <- RequestParams.from(input)
        out <- manager.findTopProducts(configs.path, params)
      } yield Response.json(out.toList.toJsonPretty)).provide(Has(blocking))
  }

  private def server: Server[Any, Throwable] = Server.port(PORT) ++ Server.paranoidLeakDetection ++ Server.app(expiredDocApp)

  val run: IO[Any, Nothing] =
    makeServer
      .provideCustomLayer(service.server.ServerChannelFactory.auto ++ EventLoopGroup.auto(threadCount) ++ zio.console.Console.live)
      .provide(Has(clock) ++ Has(console) ++ Has(system) ++ Has(random) ++ Has(blocking))

  private def makeServer: ZIO[EventLoopGroup with ServerChannelFactory with Console, Throwable, Nothing] =
    server.make.use(_ => console.putStrLn(s"Server started on port $PORT") *> ZIO.never)

}

object ReviewManagerServer {
  val live = (
    for {
      configs <- ZIO.service[CoreConfigs]
      clock <- ZIO.service[Clock.Service]
      console <- ZIO.service[Console.Service]
      system <- ZIO.service[System.Service]
      random <- ZIO.service[Random.Service]
      blocking <- ZIO.service[Blocking.Service]
      manager <- ZIO.service[AmazonReviewManager.Service]
    } yield new ReviewManagerServer(configs, clock, console, system, random, blocking, manager)
    ).toLayer

  def run: ZIO[Has[ReviewManagerServer], Any, Nothing] = ZIO.serviceWith[ReviewManagerServer](_.run)

  implicit class ToErrChannel(input: ZIO[Any, Throwable, Either[String, RawRequestParams]]) {
    def toErrChannel: ZIO[Any, Throwable, RawRequestParams] = input.flatMap {
      case Left(e) => ZIO.fail(throw new Exception(e))
      case Right(v) => ZIO.succeed(v)
    }
  }

}

