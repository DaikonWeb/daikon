package daikon

import daikon.Method.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.log.Log
import org.eclipse.jetty.util.resource.Resource
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit.MILLIS

class HttpServer(private val port: Int = 4545, initializeActions: HttpServer.() -> Unit = {}) : AutoCloseable {

    private val routes = Routing()
    private val beforeActions = Routing()
    private val afterActions = Routing()
    private val afterStartActions = mutableListOf<(Context) -> Unit>()
    private val beforeStopActions = mutableListOf<(Context) -> Unit>()
    private val basePath = mutableListOf("")
    private val context = ServerContext()

    init {
        initializeActions()
        disableJettyLog()
    }

    private lateinit var server: Server
    private val handler = ServletContextHandler()

    fun start(): HttpServer {
        val beginStarting = now()
        server = Server(port)
        handler.addServlet(ServletHolder(RoutingServlet(beforeActions, routes, afterActions, context)), "/*")
        server.handler = handler
        server.start()
        val endStarting = now()
        println("Server up and running on port $port in ${beginStarting.until(endStarting, MILLIS)}ms")
        afterStartActions.forEach { it.invoke(context) }
        return this
    }

    override fun close() {
        beforeStopActions.forEach { it.invoke(context) }
        server.stop()
    }

    fun get(path: String, action: (Request, Response) -> Unit): HttpServer {
        get(path, DummyRouteAction(action))
        return this
    }

    fun get(path: String, action: (Request, Response, Context) -> Unit): HttpServer {
        get(path, ContextRouteAction(action))
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

    fun post(path: String, action: (Request, Response, Context) -> Unit): HttpServer {
        post(path, ContextRouteAction(action))
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

    fun head(path: String, action: (Request, Response, Context) -> Unit): HttpServer {
        head(path, ContextRouteAction(action))
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

    fun any(path: String, action: (Request, Response, Context) -> Unit): HttpServer {
        any(path, ContextRouteAction(action))
        return this
    }

    fun any(path: String, action: RouteAction): HttpServer {
        add(ANY, path, action)
        return this
    }

    fun before(path: String = "/*", action: (Request, Response) -> Unit): HttpServer {
        beforeActions.add(Route(ANY, joinPaths(path), DummyRouteAction(action)))
        return this
    }

    fun before(path: String = "/*", action: (Request, Response, Context) -> Unit): HttpServer {
        beforeActions.add(Route(ANY, joinPaths(path), ContextRouteAction(action)))
        return this
    }

    fun after(path: String = "/*", action: (Request, Response) -> Unit): HttpServer {
        afterActions.add(Route(ANY, joinPaths(path), DummyRouteAction(action)))
        return this
    }

    fun after(path: String = "/*", action: (Request, Response, Context) -> Unit): HttpServer {
        afterActions.add(Route(ANY, joinPaths(path), ContextRouteAction(action)))
        return this
    }

    fun assets(path: String): HttpServer {
        val servletHolder = ServletHolder(DefaultServlet())
        handler.addServlet(servletHolder, path)
        handler.baseResource = Resource.newResource(HttpServer::class.java.getResource("/assets/"))
        return this
    }

    fun path(path: String, nested: HttpServer.() -> Unit): HttpServer {
        basePath.add(path)
        nested.invoke(this)
        basePath.removeAt(basePath.size - 1)
        return this
    }

    fun afterStart(function: (Context) -> Unit): HttpServer {
        afterStartActions.add(function)
        return this
    }

    fun beforeStop(function: (Context) -> Unit): HttpServer {
        beforeStopActions.add(function)
        return this
    }

    private fun add(method: Method, path: String, action: RouteAction) {
        routes.add(Route(method, joinPaths(path), action))
    }

    private fun joinPaths(path: String) = basePath.joinToString(separator = "") + path

    private fun disableJettyLog() {
        Log.getProperties().setProperty("org.eclipse.jetty.util.log.announce", "false")
        Log.getProperties().setProperty("org.eclipse.jetty.LEVEL", "OFF")
    }
}

