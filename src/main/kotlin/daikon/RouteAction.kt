package daikon

interface RouteAction {
    fun handle(request: Request, response: Response)
}
