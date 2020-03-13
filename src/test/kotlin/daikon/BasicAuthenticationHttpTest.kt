package daikon

import daikon.Localhost.get
import khttp.structures.authorization.BasicAuthorization
import org.assertj.core.api.Assertions.assertThat
import daikon.HttpStatus.OK_200
import daikon.HttpStatus.UNAUTHORIZED_401
import org.junit.jupiter.api.Test

class BasicAuthenticationHttpTest {

    @Test
    fun routing() {
        HttpServer()
            .get("/") { _, res -> res.status(OK_200)}
            .basicAuth("/foo", "realm")
            .basicAuth("/bar*")
            .basicAuth("/baz/:name")
            .start().use {
                assertThat(get("/").statusCode).isEqualTo(OK_200)
                assertThat(get("/foo").statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(get("/bar/baz").statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(get("/baz/alex").statusCode).isEqualTo(UNAUTHORIZED_401)
            }
    }

    @Test
    fun `authenticate user`() {
        HttpServer()
            .basicAuthUser("Marco", "secret")
            .basicAuth("/")
            .get("/") { _, res -> res.status(OK_200)}
            .start().use {
                assertThat(get("/").statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(get("/", auth = BasicAuthorization("Marco", "secret")).statusCode).isEqualTo(OK_200)
            }
    }

    @Test
    fun `wrong credential`() {
        HttpServer()
            .basicAuthUser("Marco", "secret")
            .basicAuth("/")
            .get("/") { _, res -> res.status(OK_200)}
            .start().use {
                assertThat(get("/", auth = BasicAuthorization("Marco", "wrong")).statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(get("/", auth = BasicAuthorization("wrong", "secret")).statusCode).isEqualTo(UNAUTHORIZED_401)
            }
    }

    @Test
    fun `supports utf-8`() {
        HttpServer()
            .basicAuthUser("ìù", "èéàò")
            .basicAuth("/")
            .get("/") { _, res -> res.status(OK_200)}
            .start().use {
                assertThat(get("/", auth = BasicAuthorization("ìù", "èéàò")).statusCode).isEqualTo(OK_200)
            }
    }
}