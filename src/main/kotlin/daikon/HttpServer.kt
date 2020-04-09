package daikon

import daikon.core.RoutingHandler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.log.Log
import org.eclipse.jetty.util.resource.Resource
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit.MILLIS


class HttpServer(private val port: Int = 4545, initializeActions: DaikonServer.() -> Unit = {}): DaikonServer(port, initializeActions) {
    init {
        disableJettyLog()
    }

    private lateinit var server: Server
    private val handler = ServletContextHandler()

    override fun start(routingHandler: RoutingHandler): HttpServer {
        val beginStarting = now()
        server = Server(port)
        handler.addServlet(ServletHolder(RoutingServlet(routingHandler)), "/*")
        server.handler = handler
        server.start()
        val endStarting = now()
        println("Server up and running on port $port in ${beginStarting.until(endStarting, MILLIS)}ms")
        return this
    }

    override fun stop() {
        server.stop()
    }

    fun assets(path: String): DaikonServer {
        val servletHolder = ServletHolder(DefaultServlet())
        handler.addServlet(servletHolder, path)
        handler.baseResource = Resource.newResource(HttpServer::class.java.getResource("/assets/"))
        return this
    }

    private fun disableJettyLog() {
        Log.getProperties().setProperty("org.eclipse.jetty.util.log.announce", "false")
        Log.getProperties().setProperty("org.eclipse.jetty.LEVEL", "OFF")
    }
}

