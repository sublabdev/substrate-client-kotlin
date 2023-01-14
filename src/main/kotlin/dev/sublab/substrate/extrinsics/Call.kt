package dev.sublab.substrate.extrinsics

import kotlin.reflect.KClass

/**
 * An extrinsic call
 */
open class Call<T: Any>(
    internal val moduleName: String,
    internal val name: String,
    internal val value: T,
    internal val type: KClass<T>
)