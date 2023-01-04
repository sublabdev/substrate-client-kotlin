package dev.sublab.substrate

enum class SubstrateClientNamingPolicy {
    NONE,
    CASE_INSENSITIVE;

    internal fun equals(lhs: String, rhs: String) = when (this) {
        NONE -> lhs == rhs
        CASE_INSENSITIVE -> lhs.lowercase() == rhs.lowercase()
    }
}