package org.kisobran.top

import java.util.UUID

import akka.http.scaladsl.server.Directives
import org.kisobran.top.db.TopListEntries
import org.kisobran.top.model.{Entry, TopList}
import org.kisobran.top.repository.TopListRepository
import org.kisobran.top.shared.SharedMessages
import org.kisobran.top.twirl.Implicits._

import scala.concurrent.ExecutionContext

class WebService(topListRepository: TopListRepository)(implicit executionContext: ExecutionContext) extends Directives {

  val route = {
    pathSingleSlash {
      get {
        complete {
          topListRepository.getAll().map { all =>
            org.kisobran.top.html.index.render(all)
          }
        }
      }
    } ~
      pathPrefix("glasaj") {
        get {
          complete {
            org.kisobran.top.html.glasaj.render(SharedMessages.itWorks)
          }
        }
      } ~
      pathPrefix("assets" / Remaining) { file =>
        // optionally compresses the response with Gzip or Deflate
        // if the client accepts compressed responses
        encodeResponse {
          getFromResource("public/" + file)
        }
      } ~ {
      pathPrefix("vote") {
        post {
          formFieldMap { formContent =>
            complete {

              val entries = (1 to 10).map { index =>
                Entry(formContent(s"inputArtist${index}"), formContent(s"inputSong${index}"))
              }
              topListRepository.createTopList(formContent.get("email"), entries, formContent.getOrElse("listName", s"untilted-${UUID.randomUUID()}"))

              org.kisobran.top.html.voted.render()
            }
          }
        }
      }
    }
  }
}
