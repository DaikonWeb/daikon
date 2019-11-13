package daikon

data class Route(val method: Method, val path: String, val action: (Request, Response) -> Unit)
