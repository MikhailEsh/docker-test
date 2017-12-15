package models

import java.sql.Date

import scala.collection.mutable.ListBuffer

trait ParserCSV[T <: Product] {

  implicit def convertToDate(value: String): Date = Date.valueOf(value)

  def apply(csv: String): List[T] = {
    val listCsv = csv.split("\n").to[ListBuffer]
    implicit val headers = listCsv.head.split(",").toList
    listCsv.remove(0)
    listCsv.map(f => {
      val values = f.split(",").toList
      mappingCSV(values)
    }).toList
  }

  protected def getValue(nameFields:String, values: List[String])(implicit headers:List[String]) = values(headers.indexOf(nameFields))


  protected def mappingCSV(values: List[String])(implicit headers:List[String]) : T

}
