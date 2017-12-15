package db

import scalikejdbc.WrappedResultSet
import scala.reflect.runtime.universe._

trait DbSupport[T <: Product] extends MetaSupport {

  implicit def convBigDecimal(value:Option[java.math.BigDecimal]): Option[BigDecimal] =
    value match {
      case None => None
      case value => Option(BigDecimal(value.get))
    }

  def table[T <: Product : TypeTag]: String = camelToSnake(typeOf[T].typeSymbol.name.toString.trim)

  def sqlStatement[T <: Product : TypeTag]: String = s"SELECT * FROM ${table[T]}"

  def columns[T <: Product : TypeTag]: List[String] = fieldNames[T].map(camelToSnake)

  def fieldsToColumns[T <: Product : TypeTag]: Map[String, String] = fieldNames[T].zip(columns[T]).toMap

  def camelToSnake(s: String): String = {
    val draft = "[A-Z]".r.replaceAllIn(s, { m => "_" + m.group(0).toLowerCase() })
    if(draft.startsWith("_"))
      draft.drop(1)
    else
      draft
  }

  def apply(rs: WrappedResultSet): T
}
