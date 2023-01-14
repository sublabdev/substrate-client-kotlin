package dev.sublab.substrate.extrinsics

class RuntimeCallUnknownException: Throwable()
class RuntimeVersionNotKnownException: Throwable()
class GenesisHashNotKnownException: Throwable()
class NonceNotKnownException: Throwable()
class ExtrinsicBuildFailedDueToLookupFailureException: Throwable()