package daikon

import daikon.Method.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource
import java.util.*
import javax.servlet.DispatcherType.REQUEST


class HttpServer(private val port: Int = 4545) : AutoCloseable {

    private val afterActions = mutableListOf<(Request, Response) -> Unit>()
    private lateinit var server: Server
    private val handler = ServletContextHandler()

    fun start(): HttpServer {
        server = Server(port)
        server.handler = handler
        server.start()
        return this
    }

    override fun close() {
        server.stop()
    }

    fun join(): HttpServer {
        start()
        server.join()
        return this
    }

    fun get(path: String, route: (Request, Response) -> Unit): HttpServer {
        add(GET, path, route)
        return this
    }

    fun post(path: String, route: (Request, Response) -> Unit): HttpServer {
        add(POST, path, route)
        return this
    }

    fun head(path: String, route: (Request, Response) -> Unit): HttpServer {
        add(HEAD, path, route)
        return this
    }

    fun before(path: String, action: (Request, Response) -> Unit): HttpServer {
        handler.addFilter(FilterHolder(ActionFilter(action)), path, EnumSet.of(REQUEST))
        return this
    }

    fun before(action: (Request, Response) -> Unit): HttpServer {
        before("/*", action)
        return this
    }

    fun after(action: (Request, Response) -> Unit): HttpServer {
        afterActions.add(action)
        return this
    }

    fun any(path: String, route: (Request, Response) -> Unit): HttpServer {
        add(ANY, path, route)
        return this
    }

    private fun add(method: Method, path: String, route: (Request, Response) -> Unit) {
        handler.addServlet(ServletHolder(RouteServlet(method, route, afterActions)), path)
    }

    fun assets(path: String): HttpServer {
        val servletHolder = ServletHolder(DefaultServlet())
        handler.addServlet(servletHolder, path)
        handler.baseResource = Resource.newResource(HttpServer::class.java.getResource("/assets/"))
        return this
    }
}
