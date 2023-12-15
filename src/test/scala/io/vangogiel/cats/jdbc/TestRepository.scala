package io.vangogiel.cats.jdbc

import cats.effect.{ Async, Blocker, ContextShift }
import io.vangogiel.cats.jdbc.JdbcConfig.QueryTimeout
import io.vangogiel.cats.jdbc.implicits.{ QueryOps, _ }

import java.sql.Connection
import scala.language.higherKinds

case class User(id: Long, name: String, surname: String)

class TestRepository[F[_]: Async](blocker: Blocker)(implicit
    val contextShift: ContextShift[F],
    val connection: Connection,
    val queryTimeout: QueryTimeout
) {
  private val insertUser = "INSERT INTO SAMPLE_USER.USERS (ID, NAME, SURNAME) VALUES (?, ?, ?)"
  private val selectUser = "SELECT * FROM SAMPLE_USER.USERS WHERE ID = ?"

  def insertUser(user: User): F[Int] =
    insertUser.prepare { statement =>
      statement.setLong(1, user.id)
      statement.setString(2, user.name)
      statement.setString(3, user.surname)
      statement.performUpdate(blocker)
    }

  def insertUserUsingStringInterpolation(user: User): F[Int] =
    s"""INSERT
       | INTO SAMPLE_USER.USERS (ID, NAME, SURNAME)
       | VALUES (${user.id}, '${user.name}', '${user.surname}')
       """.stripMargin
      .prepare { statement =>
        statement.performUpdate(blocker)
      }

  def findUser(id: Long): F[Option[User]] =
    selectUser.prepare { statement =>
      statement.setLong(1, id)
      statement.performQuery[F, Option[User]](
        rs => {
          if (rs.next())
            Some(User(rs.getLong("id"), rs.getString("name"), rs.getString("surname")))
          else
            None
        },
        blocker
      )
    }
}
