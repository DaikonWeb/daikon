package daikon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.MILLIS

class PerformanceTest {

    @Test
    fun `start in less then 400 ms`() {
        val start = LocalDateTime.now()
        HttpServer().start().use {
            val stop = LocalDateTime.now()
            val elapsedMillis = start.until(stop, MILLIS)
            assertThat(elapsedMillis).isLessThan(400)
        }
    }
}