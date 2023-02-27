package dev.sublab.substrate.support

import dev.sublab.ecdsa.Kind
import dev.sublab.ecdsa.ecdsa
import dev.sublab.ed25519.ed25519
import dev.sublab.encrypting.keys.KeyPair
import dev.sublab.sr25519.sr25519

internal fun allKeyPairFactories() = listOf(
    KeyPair.Factory.ecdsa(Kind.SUBSTRATE),
    KeyPair.Factory.ecdsa(Kind.ETHEREUM),
    KeyPair.Factory.ed25519,
    KeyPair.Factory.sr25519()
)