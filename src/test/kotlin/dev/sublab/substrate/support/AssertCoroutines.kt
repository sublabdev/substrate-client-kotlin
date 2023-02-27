package dev.sublab.substrate.support

import kotlinx.coroutines.runBlocking
import org.opentest4j.AssertionFailedError

internal inline fun <reified T : Throwable> assertThrowsBlocking(crossinline executable: suspend () -> Unit) = runBlocking {
    try {
        executable()
    } catch (throwable: Throwable) {
        if (throwable !is T) {
            val message = "Unexpected exception type thrown: $throwable"
            throw AssertionFailedError(message, throwable)
        }
    }
}