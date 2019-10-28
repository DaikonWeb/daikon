# Daikon

Daikon is a simple and minimal framework for creating web applications in Kotlin with minimal effort.
The main goals are:
* Use a framework easy to test
* Build a simple web application in less then 5 minutes
* Can run multiple instances of HTTP server at the same time

## Getting Started
```
HttpServer()
        .get("/foo") { _, res -> res.write("Hello foo") }
        .post("/bar") { _, res -> res.write("Bye bar") }
        .start().use {
            assertThat(get("/foo").text).isEqualTo("Hello foo")
            assertThat(post("/bar").text).isEqualTo("Bye bar")
        }
```

## Authors

* **[Marco Fracassi](https://github.com/fracassi-marco)**

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE Version 3 - see the [LICENSE](LICENSE) file for details