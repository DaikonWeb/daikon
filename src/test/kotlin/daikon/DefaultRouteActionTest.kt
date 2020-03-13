package daikon

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import daikon.core.Context
import daikon.core.DefaultRouteAction
import daikon.core.Method.GET
import daikon.core.HttpStatus.NOT_FOUND_404
import daikon.core.HttpStatus.OK_200
import daikon.core.Request
import daikon.core.Response
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.contains

class DefaultRouteActionTest {

    private val request: Request = mock()
    private val response: Response = mock()
    private val context: Context = mock()

    @Test
    fun `page not found`() {
        DefaultRouteAction().handle(request, response, context)

        verify(response).status(NOT_FOUND_404)
    }

    @Test
    fun `get on root show welcome page`() {
        whenever(request.method()).thenReturn(GET)
        whenever(request.path()).thenReturn("/")

        DefaultRouteAction().handle(request, response, context)

        verify(response).status(OK_200)
        verify(response).type("text/html")
        verify(response).write(contains("You are eating a Daikon!!"))
    }
}