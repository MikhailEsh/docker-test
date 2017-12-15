package db

import java.sql.{Date, Timestamp}

import scala.reflect.runtime.universe._

trait MetaSupport {
  def fields[T <: Product : TypeTag]: List[TermSymbol] = {
    typeOf[T].members.collect {
      case m: TermSymbol if m.isVal => m
    }.toList
  }

  def fieldTypeMap[T <: Product : TypeTag]: Map[String, Type] = {
    fields[T].map { f =>
      if(f.typeSignature.typeSymbol.fullName == "scala.Option")
        (f.name.toString.trim, f.typeSignature.typeArgs.head)
      else
        (f.name.toString.trim, f.typeSignature)
    }.toMap
  }

  def fieldNames[T <: Product : TypeTag]: List[String] = fields[T].map(_.name.toString.trim).reverse // reverse is required

  def keys: Set[String] = Set.empty
}
