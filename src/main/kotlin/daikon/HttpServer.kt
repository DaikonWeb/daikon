package daikon

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
        handler.addServlet(ServletHolder(RouteServlet(route)), path)
        return this
    }

    fun post(path: String, route: (Request, Response) -> Unit): HttpServer {
        handler.addServlet(ServletHolder(RouteServlet(route)), path)
        return this
    }
}

