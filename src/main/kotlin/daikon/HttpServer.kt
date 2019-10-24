package daikon

import daikon.Method.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder


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
}

