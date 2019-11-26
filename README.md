# Daikon

Daikon is a simple and minimal framework for creating web applications in Kotlin with minimal effort.
The main goals are:
* Use a framework easy to test
* Build a simple web application in less then 5 minutes
* Can run multiple instances of HTTP server at the same time
* Deploy application in container, with server start time less then 100ms

## How to add Daikon to your project
[![](https://jitpack.io/v/fracassi-marco/daikon.svg)](https://jitpack.io/#fracassi-marco/daikon)

### Gradle
- Add JitPack in your root build.gradle at the end of repositories:
```
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```
- Add the dependency
```
implementation 'com.github.fracassi-marco:daikon:0.4.6'
```

### Maven
- Add the JitPack repository to your build file 
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
- Add the dependency
```
<dependency>
    <groupId>com.github.fracassi-marco</groupId>
    <artifactId>daikon</artifactId>
    <version>0.4.6</version>
</dependency>
```

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

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details