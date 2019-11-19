package daikon

import daikon.Method.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource


class HttpServer(private val port: Int = 4545) : AutoCloseable {

    private lateinit var server: Server
    private val handler = ServletContextHandler()
    private val routes = Routing()
    private val befores = Routing()
    private val afters = Routing()

    fun start(): HttpServer {
        server = Server(port)
        handler.addServlet(ServletHolder(RoutingServlet(befores, routes, afters)), "/*")
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

    fun get(path: String, action: (Request, Response) -> Unit): HttpServer {
        get(path, DummyRouteAction(action))
        return this
    }

    fun get(path: String, action: RouteAction): HttpServer {
        add(GET, path, action)
        return this
    }

    fun post(path: String, action: (Request, Response) -> Unit): HttpServer {
        post(path, DummyRouteAction(action))
        return this
    }

    fun post(path: String, action: RouteAction): HttpServer {
        add(POST, path, action)
        return this
    }

    fun head(path: String, action: (Request, Response) -> Unit): HttpServer {
        head(path, DummyRouteAction(action))
        return this
    }

    fun head(path: String, action: RouteAction): HttpServer {
        add(HEAD, path, action)
        return this
    }

    fun any(path: String, action: (Request, Response) -> Unit): HttpServer {
        any(path, DummyRouteAction(action))
        return this
    }

    fun any(path: String, action: RouteAction): HttpServer {
        add(ANY, path, action)
        return this
    }

    fun before(path: String = "/*", action: (Request, Response) -> Unit): HttpServer {
        befores.add(Route(ANY, path, DummyRouteAction(action)))
        return this
    }

    fun after(path: String = "/*", action: (Request, Response) -> Unit): HttpServer {
        afters.add(Route(ANY, path, DummyRouteAction(action)))
        return this
    }

    private fun add(method: Method, path: String, action: RouteAction) {
        routes.add(Route(method, path, action))
    }

    fun assets(path: String): HttpServer {
        val servletHolder = ServletHolder(DefaultServlet())
        handler.addServlet(servletHolder, path)
        handler.baseResource = Resource.newResource(HttpServer::class.java.getResource("/assets/"))
        return this
    }
}
