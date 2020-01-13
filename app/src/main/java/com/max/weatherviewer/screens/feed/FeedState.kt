package com.max.weatherviewer.screens.feed

import com.max.weatherviewer.app.Screen
import com.max.weatherviewer.app.ScreenId
import com.max.weatherviewer.domain.Article

sealed class Feed : Screen() {
    abstract val criteria: LoadCriteria
}

sealed class LoadCriteria {

    data class Query(
        val query: String
    ) : LoadCriteria()

    object Favorite : LoadCriteria()

    object Trending : LoadCriteria()
}

data class FeedLoading(
    override val id: ScreenId,
    override val criteria: LoadCriteria
) : Feed()

data class Preview(
    override val id: ScreenId,
    override val criteria: LoadCriteria,
    val articles: List<Article>
) : Feed()

data class Error(
    override val id: ScreenId,
    override val criteria: LoadCriteria,
    val cause: Throwable
) : Feed()

// todo replace with immutable collection
fun Preview.updateArticle(
    new: Article
): Preview = copy(articles = articles.map { if (it.url == new.url) new else it })

fun Preview.prependArticle(
    new: Article
): Preview = copy(articles = listOf(new) + articles)

fun Preview.removeArticle(
    victim: Article
): Preview = copy(articles = articles.filter { it.url != victim.url })