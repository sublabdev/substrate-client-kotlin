package dev.sublab.substrate.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class JobWithTimeout(
    private val scope: CoroutineScope = CoroutineScope(Job()),
    private val timeoutMs: Long,
    private val job: suspend () -> Unit
) {

    private var lastPerform: Instant? = null

    fun perform() {
        val current = Instant.now()
        val canPerform = lastPerform?.let {
            Duration.between(current, it).toMillis() > timeoutMs
        } ?: true

        if (canPerform) {
            lastPerform = current
            scope.launch {
                job()
            }
        }
    }
}