package daikon

import daikon.core.Context
import daikon.core.Request
import daikon.core.Response
import daikon.core.RouteAction

class NopAction : RouteAction {
    override fun handle(request: Request, response: Response, context: Context) {
    }
}