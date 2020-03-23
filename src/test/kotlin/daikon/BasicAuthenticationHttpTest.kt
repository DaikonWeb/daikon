package daikon

import daikon.core.HttpStatus.OK_200
import daikon.core.HttpStatus.UNAUTHORIZED_401
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.Basic
import topinambur.http

class BasicAuthenticationHttpTest {

    @Test
    fun routing() {
        HttpServer()
            .get("/") { _, res -> res.status(OK_200)}
            .basicAuth("/foo", "realm")
            .basicAuth("/bar*")
            .basicAuth("/baz/:name")
            .start().use {
                assertThat(local("/").http.get().statusCode).isEqualTo(OK_200)
                assertThat(local("/foo").http.get().statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(local("/bar/baz").http.get().statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(local("/baz/alex").http.get().statusCode).isEqualTo(UNAUTHORIZED_401)
            }
    }

    @Test
    fun `authenticate user`() {
        HttpServer()
            .basicAuthUser("Marco", "secret")
            .basicAuth("/")
            .get("/") { _, res -> res.status(OK_200)}
            .start().use {
                assertThat(local("/").http.get().statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(local("/").http.get(auth = Basic("Marco", "secret")).statusCode).isEqualTo(OK_200)
            }
    }

    @Test
    fun `wrong credential`() {
        HttpServer()
            .basicAuthUser("Marco", "secret")
            .basicAuth("/")
            .get("/") { _, res -> res.status(OK_200)}
            .start().use {
                assertThat(local("/").http.get(auth = Basic("Marco", "wrong")).statusCode).isEqualTo(UNAUTHORIZED_401)
                assertThat(local("/").http.get(auth = Basic("wrong", "secret")).statusCode).isEqualTo(UNAUTHORIZED_401)
            }
    }

    @Test
    fun `supports utf-8`() {
        HttpServer()
            .basicAuthUser("ìù", "èéàò")
            .basicAuth("/")
            .get("/") { _, res -> res.status(OK_200)}
            .start().use {
                assertThat(local("/").http.get(auth = Basic("ìù", "èéàò")).statusCode).isEqualTo(OK_200)
            }
    }
}