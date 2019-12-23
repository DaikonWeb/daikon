package daikon

class DummyRouteAction(private val action: (Request, Response) -> Unit) : RouteAction {
    override fun handle(request: Request, response: Response, context: Context) {
        action.invoke(request, response)
    }
}

class ContextRouteAction(private val action: (Request, Response, Context) -> Unit) : RouteAction {
    override fun handle(request: Request, response: Response, context: Context) {
        action.invoke(request, response, context)
    }
}