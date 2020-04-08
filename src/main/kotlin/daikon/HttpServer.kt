package daikon

import daikon.core.*
import daikon.core.Method.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.log.Log
import org.eclipse.jetty.util.resource.Resource
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit.MILLIS
import kotlin.reflect.KClass


class HttpServer(private val port: Int = 4545, initializeActions: HttpServer.() -> Unit = {}) : AutoCloseable {

    private val routes = Routing()
    private val beforeActions = Routing()
    private val afterActions = Routing()
    private val afterStartActions = mutableListOf<(Context) -> Unit>()
    private val beforeStopActions = mutableListOf<(Context) -> Unit>()
    private val exceptions = Exceptions()
    private val basePath = mutableListOf("")
    private val context = ServerContext(port)
    private val basicAuth = BasicAuthentication()

    init {
        initializeActions()
        disableJettyLog()
    }

    private lateinit var server: Server
    private val handler = ServletContextHandler()

    fun start(): HttpServer {
        val beginStarting = now()
        server = Server(port)
        handler.addServlet(ServletHolder(RoutingServlet(beforeActions, routes, afterActions, context, exceptions)), "/*")
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

    fun exception(exception: Class<out Throwable>, action: (Request, Response, Context, Throwable) -> Unit)
            = exception(exception, ContextExceptionAction(action))

    fun exception(exception: Class<out Throwable>, action: (Request, Response, Throwable) -> Unit)
            = exception(exception, DummyExceptionAction(action))

    fun exception(exception: Class<out Throwable>, action: ExceptionAction): HttpServer {
        exceptions.add(ExceptionRoute(exception, action))
        return this
    }

    fun get(path: String, action: (Request, Response) -> Unit) = get(path, DummyRouteAction(action))

    fun get(path: String, action: (Request, Response, Context) -> Unit) = get(path, ContextRouteAction(action))

    fun get(path: String, action: RouteAction) = add(GET, path, action)

    fun post(path: String, action: (Request, Response) -> Unit) = post(path, DummyRouteAction(action))

    fun post(path: String, action: (Request, Response, Context) -> Unit) = post(path, ContextRouteAction(action))

    fun post(path: String, action: RouteAction) = add(POST, path, action)

    fun put(path: String, action: (Request, Response) -> Unit) = put(path, DummyRouteAction(action))

    fun put(path: String, action: (Request, Response, Context) -> Unit) = put(path, ContextRouteAction(action))

    fun put(path: String, action: RouteAction) = add(PUT, path, action)

    fun delete(path: String, action: (Request, Response) -> Unit) = delete(path, DummyRouteAction(action))

    fun delete(path: String, action: (Request, Response, Context) -> Unit) = delete(path, ContextRouteAction(action))

    fun delete(path: String, action: RouteAction) = add(DELETE, path, action)

    fun options(path: String, action: (Request, Response) -> Unit) = options(path, DummyRouteAction(action))

    fun options(path: String, action: (Request, Response, Context) -> Unit) = options(path, ContextRouteAction(action))

    fun options(path: String, action: RouteAction) = add(OPTIONS, path, action)

    fun head(path: String, action: (Request, Response) -> Unit) = head(path, DummyRouteAction(action))

    fun head(path: String, action: (Request, Response, Context) -> Unit) = head(path, ContextRouteAction(action))

    fun head(path: String, action: RouteAction) = add(HEAD, path, action)

    fun any(path: String, action: (Request, Response) -> Unit) = any(path, DummyRouteAction(action))

    fun any(path: String, action: (Request, Response, Context) -> Unit) = any(path, ContextRouteAction(action))

    fun any(path: String, action: RouteAction) = add(ANY, path, action)

    fun before(path: String = "/*", action: (Request, Response) -> Unit): HttpServer {
        beforeActions.add(Route(ANY, joinPaths(path), DummyRouteAction(action)))
        return this
    }

    fun before(path: String = "/*", action: (Request, Response, Context) -> Unit): HttpServer {
        beforeActions.add(Route(ANY, joinPaths(path), ContextRouteAction(action)))
        return this
    }

    fun after(path: String = "/*", action: (Request, Response) -> Unit): HttpServer {
        afterActions.add(Route(Method.ANY, joinPaths(path), DummyRouteAction(action)))
        return this
    }

    fun after(path: String = "/*", action: (Request, Response, Context) -> Unit): HttpServer {
        afterActions.add(Route(ANY, joinPaths(path), ContextRouteAction(action)))
        return this
    }

    fun assets(path: String): HttpServer {
        val servletHolder = ServletHolder(DefaultServlet())
        handler.addServlet(servletHolder, joinPaths(path))
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

    fun basicAuthUser(username: String, password: String): HttpServer {
        basicAuth.addUser(username, password)
        return this
    }

    fun basicAuth(path: String, realm: String = "default"): HttpServer {
        before(path) { req, res -> basicAuth.validate(req, res, realm) }
        return this
    }

    private fun add(method: Method, path: String, action: RouteAction): HttpServer {
        routes.add(Route(method, joinPaths(path), action))
        return this
    }

    private fun joinPaths(path: String) = basePath.joinToString(separator = "") + path

    private fun disableJettyLog() {
        Log.getProperties().setProperty("org.eclipse.jetty.util.log.announce", "false")
        Log.getProperties().setProperty("org.eclipse.jetty.LEVEL", "OFF")
    }
}

