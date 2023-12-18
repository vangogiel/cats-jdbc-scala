package io.vangogiel.cats.jdbc

import cats.effect.implicits.catsEffectSyntaxBracket
import cats.effect.{ Async, Blocker, ContextShift, Resource }
import cats.implicits.toFlatMapOps
import io.vangogiel.cats.jdbc.Assembler.{ make, makeExecuteQuery, makeExecuteUpdate }
import io.vangogiel.cats.jdbc.JdbcConfig.QueryTimeout

import java.sql.{ Connection, PreparedStatement, ResultSet }
import scala.language.higherKinds

object implicits {
  implicit class QueryOps(val query: String) extends AnyVal {
    def prepare[F[_]: Async, A](
        f: PreparedStatement => F[A]
    )(implicit c: Connection, t: QueryTimeout): F[A] =
      make {
        val st = c.prepareCall(query)
        st.setQueryTimeout(t.toSeconds.toInt)
        st
      }(f)

    def prepareInsert[F[_]: Async, A](
        columns: Seq[String] = Seq.empty
    )(f: PreparedStatement => F[A])(implicit c: Connection, t: QueryTimeout): F[A] = {
      make {
        val st = c.prepareStatement(query, columns.toArray)
        st.setQueryTimeout(t.toSeconds.toInt)
        st
      }(f)
    }
  }

  implicit class PreparedStatementOps(private val s: PreparedStatement) extends AnyVal {
    def performQuery[F[_]: Async, A](f: ResultSet => A, b: Blocker)(implicit
        cs: ContextShift[F]
    ): F[A] =
      makeExecuteQuery(b, s)(st => st.executeQuery())(f)

    def performUpdate[F[_]: Async](b: Blocker)(implicit cs: ContextShift[F]): F[Int] =
      makeExecuteUpdate(b, s)

    def performUpdate[F[_]: Async, A](
        b: Blocker
    )(f: ResultSet => A)(implicit cs: ContextShift[F]): F[A] =
      makeExecuteQuery(b, s) { st =>
        st.executeUpdate()
        st.getGeneratedKeys
      }(f)
  }
}

object Assembler {
  def makeExecuteQuery[F[_]: Async, A](b: Blocker, s: PreparedStatement)(
      f1: PreparedStatement => ResultSet
  )(f2: ResultSet => A)(implicit
      cs: ContextShift[F]
  ): F[A] =
    makeGuarantee(b, s)(st =>
      b.blockOn(Async[F].delay(f1(st))).flatMap(rs => Async[F].delay(f2(rs)))
    )

  def makeExecuteUpdate[F[_]: Async](b: Blocker, s: PreparedStatement)(implicit
      cs: ContextShift[F]
  ): F[Int] =
    makeGuarantee(b, s)(st => b.blockOn(Async[F].delay(st.executeUpdate())))

  def makeGuarantee[F[_]: Async, A](b: Blocker, s: PreparedStatement)(u: PreparedStatement => F[A])(
      implicit cs: ContextShift[F]
  ): F[A] =
    make(s)(u).guarantee(b.blockOn(Async[F].unit))

  def make[F[_]: Async, A](s: PreparedStatement)(f: PreparedStatement => F[A]): F[A] =
    Resource.make(Async[F].delay(s))(st => Async[F].delay(st.close())).use(f)
}
