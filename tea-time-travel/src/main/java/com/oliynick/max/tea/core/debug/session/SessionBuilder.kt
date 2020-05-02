@file:Suppress("FunctionName")

package com.oliynick.max.tea.core.debug.session

import com.oliynick.max.tea.core.debug.component.ServerSettings
import com.oliynick.max.tea.core.debug.component.URL
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.HttpMethod

/**
 * Function that for a given server settings opens connection
 * to a debug server and then passes debug session to a consumer
 *
 * @param M message type
 * @param S state type
 * @param J json type
 */
typealias SessionBuilder<M, S, J> = suspend (settings: ServerSettings<M, S, J>, session: suspend DebugSession<M, S, J>.() -> Unit) -> Unit

/**
 * Creates a new web socket session using supplied settings
 *
 * @param settings server settings
 * @param block lambda to interact with [session][DebugSession]
 * @param M message type
 * @param S state type
 * @param J json type
 */
suspend inline fun <reified M, reified S, J> WebSocketSession(
    settings: ServerSettings<M, S, J>,
    crossinline block: suspend DebugSession<M, S, J>.() -> Unit
) = httpClient.ws(
    method = HttpMethod.Get,
    host = settings.url.host,
    port = settings.url.port,
    block = { DebugWebSocketSession(
        M::class.java,
        S::class.java,
        settings,
        this
    ).apply { block() } }
)

@PublishedApi
internal val httpClient by lazy { HttpClient { install(WebSockets) } }

@PublishedApi
internal val localhost by lazy(::URL)
