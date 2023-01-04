package dev.sublab.substrate.extrinsics

import kotlin.reflect.KClass

open class Call<T: Any>(
    internal val moduleName: String,
    internal val name: String,
    internal val value: T,
    internal val type: KClass<T>
)