package dev.sublab.substrate.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class JobWithTimeout(
    private val scope: CoroutineScope = CoroutineScope(Job()),
    private val timeoutMs: Long,
    private val job: suspend () -> Unit
) {

    private var lastPerform: Instant? = null

    fun perform() {
        val current = Clock.System.now()
        val canPerform = lastPerform?.let {
            (current - it).inWholeMilliseconds > timeoutMs
        } ?: true

        if (canPerform) {
            lastPerform = current
            scope.launch {
                job()
            }
        }
    }
}