package filters


import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import play.api.Play.current
import play.api.Play
import play.api.mvc.{RequestHeader, Filter}
import scala.concurrent.Future
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.http.MimeTypes
import play.api.templates.Html
import play.api.libs.concurrent.Execution.Implicits._
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter


/**
 * Created by mugglmenzel on 27.03.14.
 */
class JournWeHTMLCompressorFilter(f: => HtmlCompressor) extends HTMLCompressorFilter(f) {

  def this() = this({
    val compressor = new HtmlCompressor()
    if (Play.isDev) {
      compressor.setPreserveLineBreaks(true)
    }

    compressor.setRemoveComments(true)
    compressor.setRemoveIntertagSpaces(true)
    compressor.setRemoveHttpProtocol(true)
    compressor.setRemoveHttpsProtocol(true)
    compressor
  })

}

