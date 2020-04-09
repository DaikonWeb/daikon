package daikon

import daikon.core.DaikonServer
import daikon.core.HttpStatus.OK_200
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.AbstractIntegerAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.http
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.MILLIS
import kotlin.random.Random

class PerformanceTest {

    @Test
    fun `start time benchmark`() {
        val measures = mutableListOf<Long>()

        for (i in 1..1000) {
            startServer().use {
                measures.add(elapsedMillis {
                    assertThat(local("/?a=${Random.nextInt()}").http.get().statusCode).isEqualTo(OK_200)
                })
            }
        }

        printResults(measures)
        assertThat(measures.average()).isLessThan(10.0)
        //Jetty #1000 average: 2.226 - min: 1 - max: 187
    }

    @Test
    fun `sequential requests response time benchmark`() {
        val measures = mutableListOf<Long>()

        startServer().use {
            for (i in 1..1000) {
                measures.add(elapsedMillis {
                    assertThat(local("/?a=${Random.nextInt()}").http.get().statusCode).isEqualTo(OK_200)
                })
            }
        }

        printResults(measures)
        assertThat(measures.average()).isLessThan(10.0)
        //Jetty #1000 average: 1.047 - min: 0 - max: 176
    }

    @Test
    fun `parallel requests response time benchmark`() {
        val runs = mutableListOf<Deferred<List<Long>>>()

        startServer().use {
            for (i in 1..100) {
                runs.add(GlobalScope.async {
                    val results = mutableListOf<Long>()
                    for (j in 1..100) {
                        results.add(elapsedMillis {
                            assertThat(local("/?a=${Random.nextInt()}").http.get().statusCode).isEqualTo(OK_200)
                        })
                    }

                    results
                })
            }

            runBlocking {
                val measures = mutableListOf<Long>()
                runs.map { measures.addAll(it.await()) }
                printResults(measures)
                assertThat(measures.average()).isLessThan(10.0)
            }
        }

        //Jetty #1000000 average: 0.222233 - min: 0 - max: 208
    }

    private fun printResults(measures: MutableList<Long>) {
        println("#${measures.size} average: ${measures.average()} - min: ${measures.min()} - max: ${measures.max()}")
    }

    private fun startServer(): DaikonServer {
        return HttpServer()
            .get("/") { req, res -> res.write("Hello ${req.param("a")}") }
            .start()
    }

    private fun elapsedMillis(function: () -> AbstractIntegerAssert<*>): Long {
        val start = LocalDateTime.now()
        function.invoke()
        val stop = LocalDateTime.now()
        return start.until(stop, MILLIS)
    }
}