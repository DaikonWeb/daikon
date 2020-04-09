package daikon

import daikon.core.HttpStatus.UNAUTHORIZED_401
import daikon.core.Request
import daikon.core.RequestFlow.halt
import daikon.core.Response
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

class BasicAuthentication {
    private val credentials = mutableListOf<Credential>()

    fun addUser(username: String, password: String) {
        credentials.add(Credential(username, password))
    }

    fun validate(req: Request, res: Response, realm: String) {
        try {
            val credential = credential(req.header("Authorization"))

            if (isForbidden(credential)) {
                unauthorized(res, realm)
            }
        } catch (t: Throwable) {
            unauthorized(res, realm)
        }

    }

    private fun unauthorized(res: Response, realm: String) {
        res.header("WWW-Authenticate", """Basic realm="$realm", charset="UTF-8"""")
        halt(res, UNAUTHORIZED_401)
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
