package models

import db.DbSupport
import scalikejdbc.WrappedResultSet

import scala.reflect.runtime.universe

case class User
(
  name: String,
  age: Int
)

object User extends DbSupport[User] with ParserCSV[User] {

  override def apply(rs: WrappedResultSet) =
    User(
      rs.string("name"),
      rs.int("age")
    )

  override protected def mappingCSV(values: List[String])(implicit headers: List[String]): User = new User(
    getValue("name", values),
    getValue("age", values).toInt
  )
}
