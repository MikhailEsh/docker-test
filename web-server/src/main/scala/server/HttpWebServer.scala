package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import db.CreatorSession
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import models.User
import scalikejdbc.DBSession
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object HttpWebServer extends App with LazyLogging {


  implicit val actorSystem = ActorSystem("system")
  implicit val actorMaterializer = ActorMaterializer()
  implicit val session: DBSession = CreatorSession.session
  val repo = new Repository()


  val route =
    get {
      path("get" / "all" / "users" ~ Slash.?) {
        val resp = repo.getAll("users")
        complete(StatusCodes.OK, resp)
      } ~ path("get" / "all" / "users1" ~ Slash.?) {
        val resp = repo.getAll("users1")
        complete(StatusCodes.OK, resp)
      } ~ path("get" / "count" / "users" ~ Slash.?) {
        val count = repo.count("users")
        complete(StatusCodes.OK, count)
      } ~ path("get" / "count" / "users1" ~ Slash.?) {
        val count = repo.count("users1")
        complete(StatusCodes.OK, count)
      }
    } ~ post {
      path("insert" / "all" / "users" ~ Slash.?) {
        entity(as[List[User]]) { users =>
          val countInserted = repo.insert(users, "users")
          println("insert / all Success")
          complete(StatusCodes.OK)
        }
      } ~ path("insert" / "all" / "users1" ~ Slash.?) {
        entity(as[List[User]]) { users =>
          val countInserted = repo.insert(users, "users1")
          println("insert / all Success")
          complete(StatusCodes.OK)
        }
      } ~ path("test" / "data" ~ Slash.?) {
        repo.createTestData("users")
        repo.createTestData("users1")
        complete(StatusCodes.OK)
      } ~ path("insert" / "csv" ~ Slash.?) {
        CustomDirectives.filesUpload(Set("users", "users1")) { f =>
          f match {
            case (mapByteSource) =>
              mapByteSource.foreach(f => {
                val table = f._1
                val byteSource = f._2._2
                byteSource.map(_.utf8String).runFold("") {
                  _ + _
                }.map(csv => {
                  User(csv)
                }).onComplete {
                  case Success(users) =>
                    val a = users
                    repo.insert(users, table)
                  case Failure(e) => throw e
                }
              })
          }
          complete(StatusCodes.OK)
        }
      }
    } ~ delete {
      path("drop" / "all") {
        repo.dropAll()
        println("drop / all Success")
        complete(StatusCodes.OK)
      }
    }
  Http().bindAndHandle(route, "0.0.0.0", 8080)
  println("Hello Guys I am run 1")


}
