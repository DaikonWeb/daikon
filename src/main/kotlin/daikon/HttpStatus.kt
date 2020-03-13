package daikon

object HttpStatus {
    const val CONTINUE_100 = 100
    const val SWITCHING_PROTOCOLS_101 = 101
    const val PROCESSING_102 = 102
    const val OK_200 = 200
    const val CREATED_201 = 201
    const val ACCEPTED_202 = 202
    const val NON_AUTHORITATIVE_INFORMATION_203 = 203
    const val NO_CONTENT_204 = 204
    const val RESET_CONTENT_205 = 205
    const val PARTIAL_CONTENT_206 = 206
    const val MULTI_STATUS_207 = 207
    const val MULTIPLE_CHOICES_300 = 300
    const val MOVED_PERMANENTLY_301 = 301
    const val MOVED_TEMPORARILY_302 = 302
    const val SEE_OTHER_303 = 303
    const val NOT_MODIFIED_304 = 304
    const val USE_PROXY_305 = 305
    const val TEMPORARY_REDIRECT_307 = 307
    const val PERMANENT_REDIRECT_308 = 308
    const val BAD_REQUEST_400 = 400
    const val UNAUTHORIZED_401 = 401
    const val PAYMENT_REQUIRED_402 = 402
    const val FORBIDDEN_403 = 403
    const val NOT_FOUND_404 = 404
    const val METHOD_NOT_ALLOWED_405 = 405
    const val NOT_ACCEPTABLE_406 = 406
    const val PROXY_AUTHENTICATION_REQUIRED_407 = 407
    const val REQUEST_TIMEOUT_408 = 408
    const val CONFLICT_409 = 409
    const val GONE_410 = 410
    const val LENGTH_REQUIRED_411 = 411
    const val PRECONDITION_FAILED_412 = 412
    const val PAYLOAD_TOO_LARGE_413 = 413
    const val URI_TOO_LONG_414 = 414
    const val UNSUPPORTED_MEDIA_TYPE_415 = 415
    const val RANGE_NOT_SATISFIABLE_416 = 416
    const val EXPECTATION_FAILED_417 = 417
    const val IM_A_TEAPOT_418 = 418
    const val ENHANCE_YOUR_CALM_420 = 420
    const val MISDIRECTED_REQUEST_421 = 421
    const val UNPROCESSABLE_ENTITY_422 = 422
    const val LOCKED_423 = 423
    const val FAILED_DEPENDENCY_424 = 424
    const val UPGRADE_REQUIRED_426 = 426
    const val PRECONDITION_REQUIRED_428 = 428
    const val TOO_MANY_REQUESTS_429 = 429
    const val REQUEST_HEADER_FIELDS_TOO_LARGE_431 = 431
    const val UNAVAILABLE_FOR_LEGAL_REASONS_451 = 451
    const val INTERNAL_SERVER_ERROR_500 = 500
    const val NOT_IMPLEMENTED_501 = 501
    const val BAD_GATEWAY_502 = 502
    const val SERVICE_UNAVAILABLE_503 = 503
    const val GATEWAY_TIMEOUT_504 = 504
    const val HTTP_VERSION_NOT_SUPPORTED_505 = 505
    const val INSUFFICIENT_STORAGE_507 = 507
    const val LOOP_DETECTED_508 = 508
    const val NOT_EXTENDED_510 = 510
    const val NETWORK_AUTHENTICATION_REQUIRED_511 = 511

    fun hasNoBody(code: Int): Boolean {
        return when (code) {
            NO_CONTENT_204, NOT_MODIFIED_304, PARTIAL_CONTENT_206 -> true
            else -> code < OK_200
        }
    }

    fun isInformational(code: Int): Boolean {
        return code in 100..199
    }

    fun isSuccess(code: Int): Boolean {
        return code in 200..299
    }

    fun isRedirection(code: Int): Boolean {
        return code in 300..399
    }

    fun isClientError(code: Int): Boolean {
        return code in 400..499
    }

    fun isServerError(code: Int): Boolean {
        return code in 500..599
    }
}