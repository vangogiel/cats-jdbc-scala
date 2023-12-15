package io.vangogiel.cats.jdbc

import oracle.jdbc.OracleDriver
import oracle.jdbc.pool.OracleDataSource

import java.sql.{ Connection, DriverManager }
import java.util.Properties

object JdbcConnection {
  def create(config: JdbcConfig): Connection = {
    DriverManager.registerDriver(new OracleDriver())
    val ods = new OracleDataSource()
    val prop = new Properties()
    prop.setProperty("user", config.user.value)
    prop.setProperty("password", config.password.value)
    prop.setProperty("oracle.net.networkCompression", "on")
    ods.setConnectionProperties(prop)
    ods.setURL(config.url.value)
    ods.setLoginTimeout(config.queryTimeout.toSeconds.toInt)
    ods.getConnection()
  }
}
