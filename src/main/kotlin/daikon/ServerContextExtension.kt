package daikon

import daikon.core.ServerContext

fun ServerContext.port() {
    return getAttribute("port")
}