package dev.sublab.substrate.scale

/**
 * Variable type which differs from network to network, with its index to lookup for declaration
 */
annotation class DynamicType(val lookupIndex: Int)