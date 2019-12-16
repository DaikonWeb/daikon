package daikon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.System.setErr
import java.lang.System.setOut

class LogsTest {


    private val testOut = ByteArrayOutputStream()
    private val testErr = ByteArrayOutputStream()
    private val originalPrintOut = System.out
    private val originalPrintErr = System.out

    @BeforeEach
    fun before() {
       setOut(PrintStream(testOut))
       setErr(PrintStream(testErr))
    }

    @AfterEach
    fun after() {
        setOut(originalPrintOut)
        setErr(originalPrintErr)
    }

    @Test
    fun `log only the startup`() {
        HttpServer().start().use {
            assertThat(testOut.toString()).startsWith("Server up and running on port 4545 in ")
            assertThat(testErr.toString()).isEmpty()
        }
    }
}