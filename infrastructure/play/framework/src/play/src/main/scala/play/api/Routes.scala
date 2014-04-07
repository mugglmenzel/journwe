package play.api {

  /**
   * Helper utilities related to `Router`.
   */
  object Routes {

    // -- TAGS

    val ROUTE_VERB = "ROUTE_VERB"
    val ROUTE_PATTERN = "ROUTE_PATTERN"
    val ROUTE_CONTROLLER = "ROUTE_CONTROLLER"
    val ROUTE_ACTION_METHOD = "ROUTE_ACTION_METHOD"
    val ROUTE_COMMENTS = "ROUTE_COMMENTS"

    // --

    import play.core.Router._
    import play.api.mvc.RequestHeader

    /**
     * Generates a JavaScript router.
     *
     * For example:
     * {{{
     * Routes.javascriptRouter("MyRouter")(
     *   controllers.routes.javascript.Application.index,
     *   controllers.routes.javascript.Application.list,
     *   controllers.routes.javascript.Application.create
     * )
     * }}}
     *
     * And then you can use the JavaScript router as:
     * {{{
     * var routeToHome = MyRouter.controllers.Application.index()
     * }}}
     *
     * @param name the JavaScript object name
     * @param routes the routes to include in this JavaScript router
     * @return the JavaScript code
     */
    def javascriptRouter(name: String = "Router", ajaxMethod: Option[String] = Some("jQuery.ajax"))(routes: JavascriptReverseRoute*)(implicit request: RequestHeader): String = {
      javascriptRouter(name, ajaxMethod, request.host, routes: _*)
    }

    // TODO: This JS needs to be re-written as it isn't easily maintained.
    def javascriptRouter(name: String, ajaxMethod: Option[String], host: String, routes: JavascriptReverseRoute*): String = {
      """|var %s = {}; (function(_root){
             |var _nS = function(c,f,b){var e=c.split(f||"."),g=b||_root,d,a;for(d=0,a=e.length;d<a;d++){g=g[e[d]]=g[e[d]]||{}}return g}
             |var _qS = function(items){var qs = ''; for(var i=0;i<items.length;i++) {if(items[i]) qs += (qs ? '&' : '') + items[i]}; return qs ? ('?' + qs) : ''}
             |var _s = function(p,s){return p+((s===true||(s&&s.secure))?'s':'')+'://'}
             |var _wA = function(r){return {%s method:r.method,type:r.method,url:r.url,absoluteURL: function(s){return _s('http',s)+'%s'+r.url},webSocketURL: function(s){return _s('ws',s)+'%s'+r.url}}}
             |%s
             |})(%s)
          """.stripMargin.format(
        name,
        ajaxMethod.map("ajax:function(c){c=c||{};c.url=r.url;c.type=r.method;return " + _ + "(c)},").getOrElse(""),
        host,
        host,
        routes.map { route =>
          "_nS('%s'); _root.%s = %s".format(
            route.name.split('.').dropRight(1).mkString("."),
            route.name,
            route.f)
        }.mkString("\n"),
        name)
    }

  }

}
