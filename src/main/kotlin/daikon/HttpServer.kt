package daikon

import daikon.Method.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource


class HttpServer : AutoCloseable {

    private lateinit var server: Server
    private val handler = ServletContextHandler()

    fun start(): HttpServer {
        server = Server(4545)
        server.handler = handler
        server.start()
        return this
    }

    override fun close() {
        server.stop()
    }

    fun get(path: String, route: (Request, Response) -> Unit): HttpServer {
        add(GET, path, route)
        return this
    }

    fun post(path: String, route: (Request, Response) -> Unit): HttpServer {
        add(POST, path, route)
        return this
    }

    fun any(path: String, route: (Request, Response) -> Unit): HttpServer {
        add(ANY, path, route)
        return this
    }

    private fun add(method: Method, path: String, route: (Request, Response) -> Unit) {
        handler.addServlet(ServletHolder(RouteServlet(method, route)), path)
    }

    fun assets(path: String): HttpServer {
        handler.addServlet(ServletHolder(DefaultServlet()), path)
        handler.baseResource = Resource.newResource(HttpServer::class.java.getResource("/assets/"))
        return this
    }
}

