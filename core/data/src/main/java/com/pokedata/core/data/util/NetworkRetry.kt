package com.pokedata.core.data.util

import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelayMillis: Long = 500,
    maxDelayMillis: Long = 4000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelayMillis
    repeat(times - 1) {
        try {
            return block()
        } catch (e: IOException) {
            // network issue, retry
        } catch (e: HttpException) {
            val code = e.code()
            if (code != 429 && code !in 500..599) {
                throw e // client error, don't retry
            }
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
    }
    return block() // last attempt
}
