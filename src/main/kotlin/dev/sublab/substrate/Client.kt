package dev.sublab.substrate

import dev.sublab.substrate.rpcClient.RpcClient
import dev.sublab.substrate.rpcClient.rpcVersion
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class Client {

}

abstract class RpcModule(val rpcClient: RpcClient) {
    abstract val module: String

    fun <P: Any, R: Any> callMethod(method: String) = runBlocking {
        rpcClient.sendRequest<P, R> {
            this.method = "${module}_$method"
        }
    }
}

class StateRpc(rpcClient: RpcClient): RpcModule(rpcClient) {

    override val module = "state"

    fun getMetadata() = callMethod<Unit, String>("getMetadata")
}

fun test() {
    val rpcClient = RpcClient("")
    val stateRpc = StateRpc(rpcClient)

    runBlocking {
        val metadata = async { stateRpc.getMetadata() }.await()

    }
}

class AuthorRpc {
    fun sendExtrinsic() {

    }
}