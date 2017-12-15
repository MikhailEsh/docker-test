package db

import com.typesafe.config.ConfigFactory
import scalikejdbc.{AutoSession, ConnectionPool, DBSession}

object CreatorSession {

  val config = ConfigFactory.load()
  val session: DBSession = {
    val config = ConfigFactory.load()
    val driver = config.getString("jdbc.driver")
    val url = config.getString("jdbc.url")
    val user = config.getString("jdbc.user")
    val password = config.getString("jdbc.password")
    ConnectionPool.singleton(url, user, password)
    AutoSession
  }
}
