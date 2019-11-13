package daikon

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ActionFilter(private val action: (Request, Response) -> Unit) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        action.invoke(
            HttpRequest(request as HttpServletRequest),
            HttpResponse(response as HttpServletResponse)
        )
        chain.doFilter(request, response)
    }

    override fun destroy() {
    }

    override fun init(filterConfig: FilterConfig) {
    }
}