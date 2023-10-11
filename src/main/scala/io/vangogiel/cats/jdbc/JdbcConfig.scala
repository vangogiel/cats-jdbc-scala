package io.vangogiel.cats.jdbc

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.types.string.NonEmptyString
import pureconfig.ConfigReader

import scala.concurrent.duration.FiniteDuration

final case class Secret(value: String) extends AnyVal {
  override def toString: String = "[SECRET]"
}

object Secret {
  implicit val secretConfigReader: ConfigReader[Secret] = ConfigReader[String].map(n => new Secret(n))
}

case class JdbcConfig(
    url: NonEmptyString,
    user: NonEmptyString,
    password: Secret,
    queryTimeout: JdbcConfig.QueryTimeout,
    maxPoolSize: JdbcConfig.PoolSize
)

object JdbcConfig {
  type ParallelConnections = Int Refined Positive
  type QueryTimeout = FiniteDuration
  type PoolSize = Int Refined Positive
}
