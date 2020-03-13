package daikon

import daikon.Method.ANY

class RoutingHandler(
    private val befores: Routing,
    private val routes: Routing,
    private val afters: Routing,
    private val context: Context
) {
    fun execute(request: Request, response: Response) {
        try {
            befores
                .allFor(request.method(), request.path())
                .forEach { invoke(it, request, response) }

            routes
                .default(Route(ANY, "ignore", DefaultRouteAction()))
                .bestFor(request.method(), request.path())
                .also { invoke(it, request, response) }

            afters
                .allFor(request.method(), request.path())
                .forEach { invoke(it, request, response) }
        } catch (e: HaltException) {
        }
    }

    private fun invoke(route: Route, req: Request, res: Response) {
        route.action.handle(req.withPathParams(route.path), res, context)
    }
}