package daikon

import daikon.core.Context

class ServerContext(private val port: Int) : Context {

    private val attributes = mutableMapOf<String, Any>()

    override fun addAttribute(key: String, value: Any) {
        attributes[key] = value
    }

    override fun <T> getAttribute(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return attributes[key] as T
    }

    override fun port(): Int {
        return port
    }
}
