package com.example.paycurrencyexchange.utils

import java.util.concurrent.TimeUnit

class RateLimiter(
    timeout: Int,
    timeUnit: TimeUnit
) {
    private val timeout = timeUnit.toMillis(timeout.toLong())

    @Synchronized
    fun shouldFetch(lastFetched: Long?): Boolean {
        val now = now()
        if (lastFetched == null) {
            return true
        }
        val diff = now - lastFetched
        return diff > timeout
    }

    private fun now() = System.currentTimeMillis()
}