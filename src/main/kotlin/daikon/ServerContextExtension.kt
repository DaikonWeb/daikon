package daikon

import daikon.core.Context

fun Context.port() {
    return getAttribute("port")
}