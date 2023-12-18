package io.vangogiel.cats.jdbc

import cats.effect.{ Blocker, ContextShift, IO }
import io.vangogiel.cats.jdbc.JdbcConfig.QueryTimeout
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.sql.Connection
import scala.concurrent.ExecutionContext.global

class PreparedStatementSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterEach
    with OracleTestSpec {
  implicit val blocker: Blocker = Blocker.liftExecutionContext(global)
  implicit val contextShift: ContextShift[IO] = IO.contextShift(global)

  override def beforeEach(): Unit = {
    cleanupTable(blocker)(contextShift)
  }

  "prepared statement" should {
    implicit val connection: Connection = JdbcConnection.create(config)
    implicit val queryTimeout: QueryTimeout = config.queryTimeout
    val dummyRepository = new TestRepository[IO](blocker)

    "properly perform update" in {
      val sampleUser = User(2, "John", "Smith")
      val actual = for {
        count <- dummyRepository.insertUser(sampleUser)
      } yield count
      actual.map(result => result shouldBe 1).unsafeRunSync()
    }

    "properly perform insert using interpolation" in {
      val sampleUser = User(3, "John", "Smith")
      val actual = for {
        count <- dummyRepository.insertUserUsingStringInterpolation(sampleUser)
      } yield count
      actual.map(result => result shouldBe 1).unsafeRunSync()
    }

    "properly prepare and execute insert returning column value" in {
      val sampleUser = User(4, "John", "Smith")
      val actual = for {
        name <- dummyRepository.insertReturningName(sampleUser)
      } yield name
      actual.map(result => result shouldBe "John").unsafeRunSync()
    }
  }
}
