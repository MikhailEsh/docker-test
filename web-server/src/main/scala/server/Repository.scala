package server

import models.User
import scalikejdbc.{DB, DBSession, SQL}
import server.DataTestCreator.finish

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Repository(implicit session: DBSession) {

  val limit = 100

  def getAll(table: String): List[User] = {
    val query = s"SELECT * FROM $table LIMIT $limit"
    SQL(query).map(rs => User(rs)).list().apply()
  }

  def count(table: String): Int = {
    val query = s"SELECT count(*) FROM $table"
    val resp = SQL(query).map(rs => rs.int("count")).single().apply()
    resp.get
  }

  def dropAll(): Unit = {
    val queryUsers = s"TRUNCATE users,users1"
    SQL(queryUsers).update.apply()
  }


  def insert(users: List[User], table: String): Unit = {
    val query =
      s"""
         |INSERT INTO $table
         |   (name, age)
         |  VALUES ({name}, {age})""".stripMargin.trim.replaceAll("\\r?\\n", " ")
    SQL(query).batchByName(users.map(user => {
      Seq('name -> user.name,
        'age -> user.age)
    }): _*).apply()
  }

  def createTestData(table: String): Unit = {
    val count = 100 * 1000
    val testList: scala.collection.mutable.ListBuffer[User] = ListBuffer()
    for (i <- 1 to count) {
      testList += User(s"misha $i", i)
    }
    val start = System.currentTimeMillis
    insert(testList.toList, table)
    val finish = System.currentTimeMillis
    val timeConsumedMillis = finish - start
    println("insert: " + timeConsumedMillis)
  }
}
