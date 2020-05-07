package com.max.weatherviewer.serialization

import com.max.weatherviewer.app.ScreenMessage
import com.max.weatherviewer.app.State
import com.max.weatherviewer.app.serialization.PersistentListSerializer
import com.max.weatherviewer.domain.*
import com.max.weatherviewer.screens.feed.*
import com.oliynick.max.tea.core.debug.gson.Gson
import com.oliynick.max.tea.core.debug.protocol.*
import io.kotlintest.shouldBe
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.net.URL
import java.util.*

@RunWith(JUnit4::class)
class AppStateSerializationTest {

    private val gsonSerializer = Gson {
        setPrettyPrinting()
        registerTypeHierarchyAdapter(PersistentList::class.java, PersistentListSerializer)
    }

    private val previewScreenState = Preview(
        UUID.randomUUID(),
        LoadCriteria.Query("android"),
        listOf(
            Article(
                URL("http://www.google.com"),
                Title("test"),
                null,
                Description("test"),
                null,
                Date(),
                false
            )
        )
    )

    private val loadingScreenState = FeedLoading(
        UUID.randomUUID(),
        LoadCriteria.Query("test")
    )

    private val testState = State(
        persistentListOf(
            previewScreenState,
            loadingScreenState
        )
    )

    @Test
    fun `test NotifyComponentAttached is serializing correctly`() = with(gsonSerializer) {

        val message = NotifyComponentAttached(toJsonTree(testState))
        val json = toJson(message)

        val fromJson = fromJson(json, ServerMessage::class.java)

        fromJson shouldBe message
    }

    @Test
    fun `test NotifyComponentSnapshot is serializing correctly`() = with(gsonSerializer) {

        val message = NotifyComponentSnapshot(
                toJsonTree("Message"),
                toJsonTree(testState),
                toJsonTree(loadingScreenState)
        )

        val json = toJson(message)
        val fromJson = fromJson(json, ServerMessage::class.java)

        fromJson shouldBe message
    }

    @Test
    fun `test ScreenMessage is serializing correctly`() = with(gsonSerializer) {

        val message = LoadArticles(UUID.randomUUID())

        val json = toJson(message)
        val fromJson = fromJson(json, ScreenMessage::class.java)

        fromJson shouldBe message
    }

}
