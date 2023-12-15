package io.vangogiel.cats.jdbc

import cats.effect.{ Blocker, ContextShift, IO }
import eu.timepit.refined.types.all.NonEmptyString
import eu.timepit.refined.auto._
import io.vangogiel.cats.jdbc.JdbcConfig.QueryTimeout
import io.vangogiel.cats.jdbc.implicits.{ PreparedStatementOps, QueryOps }

import java.sql.Connection
import scala.concurrent.duration.DurationInt

trait OracleTestSpec {
  def config: JdbcConfig = {
    JdbcConfig(
      url = NonEmptyString.unsafeFrom("jdbc:oracle:thin:@//localhost:1521/XEPDB1"),
      user = NonEmptyString.unsafeFrom("sample_user"),
      password = Secret("sample_user"),
      queryTimeout = 5.seconds,
      maxPoolSize = 10
    )
  }

  def cleanupTable(blocker: Blocker)(implicit contextShift: ContextShift[IO]): Unit = {
    implicit val connection: Connection = JdbcConnection.create(config)
    implicit val queryTimeout: QueryTimeout = config.queryTimeout
    "delete from sample_user.users"
      .prepare {
        _.performUpdate[IO](blocker)
      }
      .unsafeRunSync()
  }
}
