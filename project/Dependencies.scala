import sbt._

object Dependencies {
  private val ZIOVersion = "1.0.15"
  private val slf4jVersion = "1.7.25"
  
  case object dev {
    case object zio {
      val zio = "dev.zio" %% "zio" % ZIOVersion
      val streams = "dev.zio" %% "zio-streams" % ZIOVersion
      val test = "dev.zio" %% "zio-test" % ZIOVersion
      val testSbt = "dev.zio" %% "zio-test-sbt" % ZIOVersion
      val logging = "dev.zio" %% "zio-logging" % "0.5.9"
      val config = "dev.zio" %% "zio-config" % "1.0.10"
      val configDev = "dev.zio" %% "zio-config-derivation" % "1.0.10"
      val configTypesafe = "dev.zio" %% "zio-config-typesafe" % "1.0.10"
      val json = "dev.zio" %% "zio-json" % "0.1.5"
      val prelude = "dev.zio" %% "zio-prelude" % "1.0.0-RC7"
    }
  }
  case object org {
    case object slf4j {
      val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jVersion
      val slf4jLog4j12 = "org.slf4j" % "slf4j-log4j12" % slf4jVersion
    }
    case object apache {
      case object logging {
        case object log4j {
          val core = "org.apache.logging.log4j" % "log4j-core" % "2.13.3"
          val slf4jImlp = "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.13.3"
        }
      }
    }
  }
  case object ioo {
    case object d11 {
      val zhttp = "io.d11" % "zhttp_2.13" % "1.0.0.0-RC23"
    }
  }
}

