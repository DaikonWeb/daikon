package daikon

import daikon.core.Context

fun Context.port(): Int {
    return getAttribute("port")
}