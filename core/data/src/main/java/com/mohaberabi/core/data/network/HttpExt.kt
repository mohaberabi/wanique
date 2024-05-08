package com.mohaberabi.core.data.network

import com.mohaberabi.core.data.BuildConfig
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.error.DataError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.cio.Request
import kotlinx.serialization.SerializationException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException


suspend inline fun <reified Response : Any> HttpClient.get(
    route: String,
    queryParams: Map<String, Any?> = mapOf()
): AppResult<Response, DataError.Network> {

    return safeCall {
        get {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }
}

suspend inline fun <reified Response : Any> HttpClient.delete(
    route: String,
    queryParams: Map<String, Any?> = mapOf()
): AppResult<Response, DataError.Network> {
    return safeCall {
        delete {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }
}

suspend inline fun <reified Request, reified Response : Any> HttpClient.post(
    route: String,
    body: Request
): AppResult<Response, DataError.Network> {

    return safeCall {
        post {
            url(constructRoute(route))
            setBody(body)
        }
    }
}

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): AppResult<T, DataError.Network> {

    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        e.printStackTrace()
        return AppResult.Error(DataError.Network.NO_NETWORK)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return AppResult.Error(DataError.Network.SERIALIZATION_ERROR)
        /**
         * careful with try {}catch {} blocks inside of a suspend function
         * because when exception is thrown to coroutine it needs to be probagated up to the parents coroutines [CancellationException]
         * this is a handled exception by default but if you did catch(e:Exception) this [CancellationException] is no longer auto handeled
         * and it is handled by the catch(e:Excption) so the parents will not ever know that the child has thrown exception
         * so the best solution where  to check if it was [CancellationException] rethrow it again
         */
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        return AppResult.Error(DataError.Network.UNKNOWN_ERROR)

    }
    return mapResponseToAppResult(response)
}


suspend inline fun <reified T> mapResponseToAppResult(response: HttpResponse): AppResult<T, DataError.Network> {


    return when (response.status.value) {
        in 200..299 -> AppResult.Done(response.body<T>())
        401 -> AppResult.Error(DataError.Network.UNAUTHORIZED)
        408 -> AppResult.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> AppResult.Error(DataError.Network.CONFLICT)
        413 -> AppResult.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> AppResult.Error(DataError.Network.TOO_MANY_REQUEST)
        in 500..599 -> AppResult.Error(DataError.Network.SERVER_ERROR)
        else -> AppResult.Error(DataError.Network.UNKNOWN_ERROR)

    }
}

fun constructRoute(route: String): String {


    return when {
        route.contains(BuildConfig.BASE_URL) -> route
        route.startsWith("/") -> BuildConfig.BASE_URL + route
        else -> BuildConfig.BASE_URL + "/$route"

    }
}