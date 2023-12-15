package io.vangogiel.cats.jdbc

import eu.timepit.refined.pureconfig._
import eu.timepit.refined.types.string.NonEmptyString
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class JdbcConfigSpec extends AnyWordSpec with Matchers {

  "jdbc config" should {
    "be properly loaded from the file" in {
      val config: JdbcConfig =
        ConfigSource.resources("jdbc-config.conf").load[JdbcConfig].getOrElse {
          throw new Throwable("Error loading test config file")
        }

      config.password.toString shouldBe "[SECRET]"
      config.user shouldBe NonEmptyString.unsafeFrom("sample_user")
    }
  }
}
