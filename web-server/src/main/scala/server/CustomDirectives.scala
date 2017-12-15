package server

import akka.http.scaladsl.model.Multipart
import akka.http.scaladsl.server.{Directive1, MissingFormFieldRejection}
import akka.http.scaladsl.server.directives.BasicDirectives.{extractRequestContext, provide}
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.directives.FutureDirectives.onSuccess
import akka.http.scaladsl.server.directives.MarshallingDirectives.{as, entity}
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString

object CustomDirectives {

  def filesUpload(fieldsName: Set[String]): Directive1[Map[String, (FileInfo, Source[ByteString, Any])]] =
    entity(as[Multipart.FormData]).flatMap { formData ⇒
      extractRequestContext.flatMap { ctx ⇒
        implicit val mat = ctx.materializer
        implicit val ec = ctx.executionContext

        val filesPartSource: Source[(String, (FileInfo, Source[ByteString, Any])), Any] = formData.parts
          .filter(part ⇒ part.filename.isDefined && fieldsName.contains(part.name))
          .map(part ⇒ part.name -> (FileInfo(part.name, part.filename.get, part.entity.contentType), part.entity.dataBytes))


//        val onePartSource: Source[(FileInfo, Source[ByteString, Any]), Any] = formData.parts
//          .filter(part ⇒ part.filename.isDefined && part.name == fieldName)
//          .map(part ⇒ (FileInfo(part.name, part.filename.get, part.entity.contentType), part.entity.dataBytes))
//          .take(1)
//        val onePartF = filesPartSource.runWith(Sink.headOption[(String, (FileInfo, Source[ByteString, Any]))])
        val onePartF = filesPartSource.runWith(Sink.seq[(String, (FileInfo, Source[ByteString, Any]))])

        onSuccess(onePartF)
      }

    }.flatMap {f =>
      provide(f.toMap)
//      case Some(tuple) ⇒
//        val a = tuple
//        provide(tuple)
//      case None ⇒ reject(MissingFormFieldRejection(fieldName))
    }
}
