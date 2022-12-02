package dev.sublab.substrate.support

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object Constants {
    const val testsCount = 1000
    val singleTestTimeout = 3.seconds
    val testsTimeout = 1.minutes

    const val webSocketUrl = "echo.ws.sublab.dev"
    const val webSocketPort = 8023

    const val resourcesPath = "src/test/resources/"
}