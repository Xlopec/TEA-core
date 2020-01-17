@file:Suppress("FunctionName")

package com.max.weatherviewer.app

import com.max.weatherviewer.app.env.Environment
import com.max.weatherviewer.app.serialization.PersistentListSerializer
import com.max.weatherviewer.screens.feed.FeedLoading
import com.max.weatherviewer.screens.feed.LoadCriteria
import com.oliynick.max.elm.core.component.Initializer
import com.oliynick.max.elm.core.component.states
import com.oliynick.max.elm.core.component.with
import com.oliynick.max.elm.core.loop.ComponentFock
import com.oliynick.max.elm.time.travel.component.Component
import com.oliynick.max.elm.time.travel.component.URL
import com.oliynick.max.elm.time.travel.converter.GsonSerializer
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow
import protocol.ComponentId
import java.util.*

fun Environment.appComponent(): (Flow<Message>) -> Flow<State> {

    suspend fun resolve(command: Command) = this.resolve(command)

    fun update(
        message: Message,
        state: State
    ) = this.update(message, state)

    // todo state persistence

    if (false && isDebug) {

        return Component(
            ComponentId("News Reader App"),
            AppInitializer(),
            ::resolve,
            ::update
        ) {
            serverSettings {
                url(URL(host = "10.0.2.2"))
                installSerializer(AppGsonSerializer())
            }
        }.states()
    }

    return ComponentFock(AppInitializer(), ::resolve, ::update).with { println(it) }.states()

}

private fun AppInitializer(): Initializer<State, Command> {

    val initScreen = FeedLoading(
        UUID.randomUUID(),
        LoadCriteria.Query("android")
    )

    return Initializer(
        State(initScreen),
        LoadByCriteria(
            initScreen.id,
            initScreen.criteria
        )
    )
}

private fun AppGsonSerializer() = GsonSerializer {
    registerTypeHierarchyAdapter(PersistentList::class.java, PersistentListSerializer)
}
