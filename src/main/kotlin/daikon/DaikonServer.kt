package daikon

import daikon.core.*

abstract class DaikonServer(port: Int = 4545, initializeActions: DaikonServer.() -> Unit = {}) : AutoCloseable {
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
    }

    protected abstract fun start(routingHandler: RoutingHandler): DaikonServer
    protected abstract fun stop()

    fun start(): DaikonServer {
        val routingHandler = RoutingHandler(beforeActions, routes, afterActions, context, exceptions)
        start(routingHandler)
        afterStartActions.forEach { it.invoke(context) }
        return this
    }

    override fun close() {
        beforeStopActions.forEach { it.invoke(context) }
        stop()
    }

    fun exception(exception: Class<out Throwable>, action: (Request, Response, Context, Throwable) -> Unit)
            = exception(exception, ContextExceptionAction(action))

    fun exception(exception: Class<out Throwable>, action: (Request, Response, Throwable) -> Unit)
            = exception(exception, DummyExceptionAction(action))

    fun exception(exception: Class<out Throwable>, action: ExceptionAction): DaikonServer {
        exceptions.add(ExceptionRoute(exception, action))
        return this
    }

    fun get(path: String, action: (Request, Response) -> Unit) = get(path, DummyRouteAction(action))

    fun get(path: String, action: (Request, Response, Context) -> Unit) = get(path, ContextRouteAction(action))

    fun get(path: String, action: RouteAction) = add(Method.GET, path, action)

    fun post(path: String, action: (Request, Response) -> Unit) = post(path, DummyRouteAction(action))

    fun post(path: String, action: (Request, Response, Context) -> Unit) = post(path, ContextRouteAction(action))

    fun post(path: String, action: RouteAction) = add(Method.POST, path, action)

    fun put(path: String, action: (Request, Response) -> Unit) = put(path, DummyRouteAction(action))

    fun put(path: String, action: (Request, Response, Context) -> Unit) = put(path, ContextRouteAction(action))

    fun put(path: String, action: RouteAction) = add(Method.PUT, path, action)

    fun delete(path: String, action: (Request, Response) -> Unit) = delete(path, DummyRouteAction(action))

    fun delete(path: String, action: (Request, Response, Context) -> Unit) = delete(path, ContextRouteAction(action))

    fun delete(path: String, action: RouteAction) = add(Method.DELETE, path, action)

    fun options(path: String, action: (Request, Response) -> Unit) = options(path, DummyRouteAction(action))

    fun options(path: String, action: (Request, Response, Context) -> Unit) = options(path, ContextRouteAction(action))

    fun options(path: String, action: RouteAction) = add(Method.OPTIONS, path, action)

    fun head(path: String, action: (Request, Response) -> Unit) = head(path, DummyRouteAction(action))

    fun head(path: String, action: (Request, Response, Context) -> Unit) = head(path, ContextRouteAction(action))

    fun head(path: String, action: RouteAction) = add(Method.HEAD, path, action)

    fun any(path: String, action: (Request, Response) -> Unit) = any(path, DummyRouteAction(action))

    fun any(path: String, action: (Request, Response, Context) -> Unit) = any(path, ContextRouteAction(action))

    fun any(path: String, action: RouteAction) = add(Method.ANY, path, action)

    fun before(path: String = "/*", action: (Request, Response) -> Unit): DaikonServer {
        beforeActions.add(Route(Method.ANY, joinPaths(path), DummyRouteAction(action)))
        return this
    }

    fun before(path: String = "/*", action: (Request, Response, Context) -> Unit): DaikonServer {
        beforeActions.add(Route(Method.ANY, joinPaths(path), ContextRouteAction(action)))
        return this
    }

    fun after(path: String = "/*", action: (Request, Response) -> Unit): DaikonServer {
        afterActions.add(Route(Method.ANY, joinPaths(path), DummyRouteAction(action)))
        return this
    }

    fun after(path: String = "/*", action: (Request, Response, Context) -> Unit): DaikonServer {
        afterActions.add(Route(Method.ANY, joinPaths(path), ContextRouteAction(action)))
        return this
    }

    fun path(path: String, nested: DaikonServer.() -> Unit): DaikonServer {
        basePath.add(path)
        nested.invoke(this)
        basePath.removeAt(basePath.size - 1)
        return this
    }

    fun afterStart(function: (Context) -> Unit): DaikonServer {
        afterStartActions.add(function)
        return this
    }

    fun beforeStop(function: (Context) -> Unit): DaikonServer {
        beforeStopActions.add(function)
        return this
    }

    fun basicAuthUser(username: String, password: String): DaikonServer {
        basicAuth.addUser(username, password)
        return this
    }

    fun basicAuth(path: String, realm: String = "default"): DaikonServer {
        before(path) { req, res -> basicAuth.validate(req, res, realm) }
        return this
    }

    private fun add(method: Method, path: String, action: RouteAction): DaikonServer {
        routes.add(Route(method, joinPaths(path), action))
        return this
    }

    private fun joinPaths(path: String) = basePath.joinToString(separator = "") + path
}