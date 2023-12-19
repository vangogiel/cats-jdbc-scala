# cats-jdbc-scala

A very lightweight library for building a functional JDBC repository layer. It provides functionality to configure OracleDataSource. The implicits offer higher-order functions to allow the creation of compact and easy-to-read database operations.

## Examples

Repository example: [TestRepository.scala](./src/test/scala/io/vangogiel/cats/jdbc/TestRepository.scala)

Prepared Statement Usage: [PreparedStatementSpec.scala](./src/test/scala/io/vangogiel/cats/jdbc/PreparedStatementSpec.scala)

Query Usage: [QueryOpsSpec.scala](./src/test/scala/io/vangogiel/cats/jdbc/QueryOpsSpec.scala)
