package server

import db.CreatorSession
import scalikejdbc.DBSession

object DataTestCreator extends App {
  implicit val session: DBSession = CreatorSession.session
  val repo = new Repository()
  val start = System.currentTimeMillis
  repo.createTestData("users")
  repo.createTestData("users1")
  val finish = System.currentTimeMillis
  val timeConsumedMillis = finish - start
  println("users: " + repo.count("users"))
//  println("users1: " + repo.count("users1"))
  println(finish - start)
  println((finish - start) / 1000)
}
