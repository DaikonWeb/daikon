package daikon

import khttp.DEFAULT_TIMEOUT
import khttp.responses.Response
import khttp.structures.authorization.Authorization
import khttp.structures.files.FileLike

object Localhost {

    fun get(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf()): Response {
        return khttp.get("http://localhost:4545$url")
    }

    fun post(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf()): Response {
        return khttp.post("http://localhost:4545$url")
    }
}