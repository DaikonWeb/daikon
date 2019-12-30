package daikon

import daikon.RequestFlow.halt
import org.eclipse.jetty.http.HttpHeader.AUTHORIZATION
import org.eclipse.jetty.http.HttpHeader.WWW_AUTHENTICATE
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

class BasicAuthentication {

    private val credentials = mutableListOf<Credential>()

    fun addUser(username: String, password: String) {
        credentials.add(Credential(username, password))
    }

    fun validate(req: Request, res: Response, realm: String) {
        try {
            val credential = credential(req.header(AUTHORIZATION.asString()))

            if (isForbidden(credential)) {
                unauthorized(res, realm)
            }
        } catch (t: Throwable) {
            unauthorized(res, realm)
        }

    }

    private fun unauthorized(res: Response, realm: String) {
        res.header(
            WWW_AUTHENTICATE.asString(),
            """Basic realm="$realm", charset="UTF-8""""
        )
        halt(res, SC_UNAUTHORIZED)
    }

    private fun isForbidden(credential: Credential): Boolean {
        return credentials.none {  it == credential  }
    }

    private fun credential(header: String): Credential {
        val credentials = String(
            Base64.getDecoder().decode(header.replace("Basic ", "", true)),
            UTF_8
        ).split(":")

        return Credential(credentials[0], credentials[1])
    }
}

data class Credential(val username: String, val password: String)
