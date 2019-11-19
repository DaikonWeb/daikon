package daikon

class DummyRouteAction(private val action: (Request, Response) -> Unit) : RouteAction {
    override fun handle(request: Request, response: Response) {
        action.invoke(request, response)
    }
}