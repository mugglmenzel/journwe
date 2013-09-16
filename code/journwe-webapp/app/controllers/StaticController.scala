package controllers

import play.api.Play.current

import play.api.mvc.Controller
import play.api.cache.Cached
import play.api.mvc.Action
import play.mvc.Http

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 16.09.13
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
object StaticController extends Controller {

  def indexNew = Cached("indexNew", 3600) {
    Action { implicit request =>
      Ok(views.html.index.indexNew.render(null))
    }
  }

  def imprint = Cached("imprint") {
    Action {
      Ok(views.html.imprint.render())
    }
  }

  def about = Cached("about") {
    Action {
      Ok(views.html.about.render())
    }
  }


  def ping = Cached("ping") {
    Action {
      Ok("pong")
    }
  }

}
