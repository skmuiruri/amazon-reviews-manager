package com.arm.config

import zio._
import zio.config._
import ConfigDescriptor._
import zio.config.typesafe.TypesafeConfigSource

case class CoreConfigs(path: String)

object CoreConfigs {
  val live: ZLayer[Any, ReadError[String], Has[CoreConfigs]] = (for {
    //envSrc <- ConfigSource.fromSystemEnv.provideLayer(zio.system.System.live)
    propsSrc <- TypesafeConfigSource.fromDefaultLoader
    configs <- ZIO.fromEither(
      read(
        string("SRC_FILE_PATH").from(propsSrc)(CoreConfigs.apply, CoreConfigs.unapply)
      )
    )
  } yield configs).toLayer
}
