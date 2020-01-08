package daikon

class PathParams(private val path: String) {

    fun path(): String {
        return path.split("/").joinToString(separator = "/") { if (it.startsWith(":")) "[^/]+" else it }
    }

    fun valueOf(ulrPath: String): Map<String, String> {
        val parts = ulrPath.split("/")
        val placeholders = path.split("/").mapIndexed { index, part -> index to part }.toMap()
        return placeholders.map { entry -> entry.value to parts[entry.key] }.toMap()
    }
}
