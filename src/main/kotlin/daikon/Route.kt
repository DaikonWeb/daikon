package daikon

data class Route(val method: Method, val path: String, val action: RouteAction) {

    private fun pathPattern() : Regex {
        if(path.contains(":")) {
            return asRegex(PathParams(path).path())
        }

        return asRegex(path)
    }

    fun matches(actualPath: String) : Boolean {
        return actualPath.matches(pathPattern())
    }

    private fun asRegex(rawPath: String) = rawPath.replace("*", ".*").toRegex()
}
