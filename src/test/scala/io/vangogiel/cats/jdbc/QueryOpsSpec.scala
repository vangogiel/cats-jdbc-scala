package io.vangogiel.cats.jdbc

import cats.effect.{ Blocker, ContextShift, IO }
import io.vangogiel.cats.jdbc.JdbcConfig.QueryTimeout
import io.vangogiel.cats.jdbc.implicits.{ PreparedStatementOps, QueryOps }
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.sql.{ Connection, ResultSet, SQLException }
import scala.concurrent.ExecutionContext.global

class QueryOpsSpec extends AnyWordSpec with Matchers with OracleTestSpec with BeforeAndAfterEach {
  implicit val blocker: Blocker = Blocker.liftExecutionContext(global)
  implicit val contextShift: ContextShift[IO] = IO.contextShift(global)

  override def beforeEach(): Unit = {
    cleanupTable(blocker)(contextShift)
  }

  "prepared statement" should {
    implicit val connection: Connection = JdbcConnection.create(config)
    implicit val queryTimeout: QueryTimeout = config.queryTimeout
    val dummyRepository = new TestRepository[IO](blocker)

    "properly perform query returning mapped result" in {
      val sampleUser = User(1, "John", "Smith")
      val actual = for {
        _ <- dummyRepository.insertUser(sampleUser)
        retrievedUser <- dummyRepository.findUser(sampleUser.id)
      } yield retrievedUser
      actual.map(result => result shouldBe Some(sampleUser)).unsafeRunSync()
    }

    "properly handle exception" in {
      assertThrows[SQLException] {
        "select * from unknown_schema.unknown_table"
          .prepare {
            _.performQuery[IO, ResultSet](rs => rs, blocker)
          }
          .unsafeRunSync()
      }
    }
  }
}
