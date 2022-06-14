package com.arm

import com.arm.config.CoreConfigs
import com.arm.domain.AmazonReviewManager
import com.arm.server.ReviewManagerServer
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.system.System
import zio.random.Random

object Main extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    val dependencies = (Blocking.live ++ Random.live ++ System.live ++ Console.live ++ Clock.live ++ CoreConfigs.live ++ AmazonReviewManager.live) >>> ReviewManagerServer.live
    ReviewManagerServer.run.provideLayer(dependencies).exitCode

  }
}
