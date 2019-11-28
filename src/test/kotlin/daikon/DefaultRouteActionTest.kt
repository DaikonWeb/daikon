package daikon

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import daikon.Method.GET
import org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404
import org.eclipse.jetty.http.HttpStatus.OK_200
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.contains

class DefaultRouteActionTest {

    private val request: Request = mock()
    private val response: Response = mock()

    @Test
    fun `page not found`() {
        DefaultRouteAction().handle(request, response)

        verify(response).status(NOT_FOUND_404)
    }

    @Test
    fun `get on root show welcome page`() {
        whenever(request.method()).thenReturn(GET)
        whenever(request.path()).thenReturn("/")

        DefaultRouteAction().handle(request, response)

        verify(response).status(OK_200)
        verify(response).content("text/html")
        verify(response).write(contains("You are eating a Daikon!!"))
    }
}