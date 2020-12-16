@file:Suppress("FunctionName")

package com.max.reader.screens.article.list.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.max.reader.app.Message
import com.max.reader.app.NavigateToArticleDetails
import com.max.reader.app.ScreenId
import com.max.reader.domain.Article
import com.max.reader.domain.Author
import com.max.reader.domain.Description
import com.max.reader.domain.Title
import com.max.reader.misc.E0
import com.max.reader.misc.E1
import com.max.reader.misc.safe
import com.max.reader.screens.article.list.*
import com.max.reader.screens.article.list.QueryType.*
import com.max.reader.ui.theme.ThemedPreview
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ArticlesScreen(
    modifier: Modifier,
    state: ArticlesState,
    onMessage: (Message) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        when (val either = state.transientState) {
            is E0 ->
                ArticlesExceptionContent(
                    id = state.id,
                    query = state.query,
                    articles = state.articles,
                    cause = either.l,
                    onMessage = onMessage
                )
            is E1 -> {
                if (either.r) {
                    ArticlesLoadingContent(state.id, state.query, state.articles, onMessage)
                } else {
                    ArticlesPreviewContent(state.id, state.query, state.articles, onMessage)
                }
            }
            // fixme remove
            else -> error("(")
        }.safe
    }
}

@Composable
private fun ArticlesProgress(
    modifier: Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ArticlesLoadingContent(
    id: ScreenId,
    query: Query,
    articles: List<Article>,
    onMessage: (Message) -> Unit,
) {
    ArticlesContent(
        id = id,
        query = query, onMessage = onMessage
    ) {
        // todo implement pagination
        item {
            ArticlesProgress(
                modifier = Modifier.fillParentMaxSize()
            )
        }

    }
}

@Composable
private fun ArticlesExceptionContent(
    id: ScreenId,
    query: Query,
    articles: List<Article>,
    cause: Throwable,
    onMessage: (Message) -> Unit,
) {
    ArticlesContent(id, query, onMessage) {

        item {
            ArticlesError(
                modifier = Modifier.fillParentMaxSize(),
                id = id,
                message = cause.toReadableMessage(),
                onMessage = onMessage
            )
        }

    }
}

@Composable
private fun ArticlesPreviewContent(
    id: ScreenId,
    query: Query,
    articles: List<Article>,
    onMessage: (Message) -> Unit,
) {
    if (articles.isEmpty()) {
        ArticlesContentEmpty(id, query, onMessage)
    } else {
        ArticlesContentNonEmpty(id, query, articles, onMessage)
    }
}

@Composable
private fun ArticlesContent(
    id: ScreenId,
    query: Query,
    onMessage: (Message) -> Unit,
    children: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            ArticleSearchHeader(
                id = id,
                query = query,
                onMessage = onMessage
            )

            Spacer(modifier = Modifier.preferredHeight(16.dp))
        }

        children()
    }
}

@Composable
private fun ArticlesContentEmpty(
    id: ScreenId,
    query: Query,
    onMessage: (Message) -> Unit,
) {
    ArticlesContent(id, query, onMessage) {

        item {
            Message(
                modifier = Modifier.fillParentMaxSize(),
                message = "Couldn't find articles",
                actionText = "Reload",
                onClick = {
                    onMessage(LoadArticles(id))
                }
            )
        }
    }
}

@Composable
private fun ArticlesContentNonEmpty(
    id: ScreenId,
    query: Query,
    articles: List<Article>,
    onMessage: (Message) -> Unit,
) {
    require(articles.isNotEmpty())

    ArticlesContent(id, query, onMessage) {

        item {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                text = query.toScreenTitle(),
                style = typography.subtitle1
            )

            Spacer(modifier = Modifier.preferredHeight(16.dp))
        }

        itemsIndexed(articles) { index, article ->
            Column {
                ArticleItem(
                    screenId = id,
                    article = article,
                    onMessage = onMessage
                )

                if (index != articles.lastIndex) {
                    Spacer(modifier = Modifier.preferredHeight(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ArticleImage(
    imageUrl: URL?,
) {
    Surface(
        modifier = Modifier
            .preferredHeight(200.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(topLeft = 8.dp, topRight = 8.dp),
        color = colors.onSurface.copy(alpha = 0.2f)
    ) {

        if (imageUrl != null) {

            CoilImage(
                modifier = Modifier.fillMaxWidth(),
                data = imageUrl.toExternalForm(),
                fadeIn = true,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ArticleItem(
    screenId: ScreenId,
    article: Article,
    onMessage: (Message) -> Unit,
) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = { onMessage(NavigateToArticleDetails(article)) })
        ) {

            ArticleImage(imageUrl = article.urlToImage)

            Spacer(modifier = Modifier.height(8.dp))

            ArticleContents(article = article)

            Spacer(modifier = Modifier.height(4.dp))

            ArticleActions(onMessage, article, screenId)
        }
    }
}

@Composable
private fun ArticleContents(
    article: Article,
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {

        Text(
            text = article.title.value,
            style = typography.h6
        )

        if (article.author != null) {
            Text(
                text = article.author.value,
                style = typography.subtitle2
            )
        }

        Text(
            text = "Published on ${DateFormatter.format(article.published)}",
            style = typography.body2
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = article.description?.value ?: "No description",
            style = typography.body2
        )
    }
}

@Composable
private fun ArticleActions(
    onMessage: (Message) -> Unit,
    article: Article,
    screenId: ScreenId,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {

        IconButton(
            onClick = { onMessage(ShareArticle(article)) }
        ) {
            Icon(imageVector = Icons.Default.Share)
        }

        IconButton(
            onClick = { onMessage(ToggleArticleIsFavorite(screenId, article)) }
        ) {
            Icon(
                imageVector = if (article.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            )
        }
    }
}

@Composable
private fun ArticlesError(
    modifier: Modifier,
    id: ScreenId,
    message: String,
    onMessage: (Message) -> Unit,
) {
    Message(
        modifier,
        "Failed to load articles, message: '${message.decapitalize(Locale.getDefault())}'",
        "Retry"
    ) {
        onMessage(LoadArticles(id))
    }
}

@Composable
private fun Message(
    modifier: Modifier,
    message: String,
    actionText: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center
        )

        TextButton(
            onClick = onClick
        ) {
            Text(text = actionText)
        }
    }
}

@OptIn(ExperimentalFocus::class)
@Composable
private fun ArticleSearchHeader(
    modifier: Modifier = Modifier,
    id: ScreenId,
    query: Query,
    onMessage: (Message) -> Unit,
) {
    Card(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {

        TextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = query.type.toSearchHint(), style = typography.subtitle2) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            value = query.input,
            maxLines = 1,
            onImeActionPerformed = { _, ctrl ->
                onMessage(LoadArticles(id)); ctrl?.hideSoftwareKeyboard()
            },
            backgroundColor = colors.surface,
            textStyle = typography.subtitle2,
            trailingIcon = {
                IconButton(
                    onClick = { onMessage(LoadArticles(id)) }
                ) {
                    Icon(imageVector = Icons.Default.Search)
                }
            },
            onValueChange = { query -> onMessage(OnQueryUpdated(id, query)) }
        )
    }
}

@Composable
@Preview("Articles search input field")
private fun ArticleSearchHeaderPreview() {
    ThemedPreview {
        ArticleSearchHeader(
            id = UUID.randomUUID(),
            query = Query("some input text", Trending),
            onMessage = {}
        )
    }
}

@Composable
@Preview("Article item")
private fun ArticleItemPreview() {
    ThemedPreview {
        ArticleItem(
            screenId = UUID.randomUUID(),
            article = ArticleSamplePreview,
            onMessage = {}
        )
    }
}

@Composable
@Preview("Articles bottom action menu")
private fun ArticleActionsPreview() {
    ThemedPreview {
        ArticleActions(
            onMessage = {},
            article = ArticleSamplePreview,
            screenId = UUID.randomUUID()
        )
    }
}

@Composable
@Preview("Messages preview")
private fun MessagePreview() {
    ThemedPreview {
        Message(
            modifier = Modifier,
            message = "Oops, something went wrong",
            actionText = "Retry",
            onClick = {}
        )
    }
}

private val DateFormatter: SimpleDateFormat by lazy {
    SimpleDateFormat("dd MMM' at 'hh:mm", Locale.getDefault())
}

private fun Throwable.toReadableMessage() =
    message?.decapitalize(Locale.getDefault()) ?: "unknown exception"

private fun Query.toScreenTitle(): String =
    when (type) {
        Regular -> "Feed"
        Favorite -> "Favorite"
        Trending -> "Trending"
    }

private fun QueryType.toSearchHint(): String =
    when (this) {
        Regular -> "Search in articles"
        Favorite -> "Search in favorite"
        Trending -> "Search in trending"
    }

private val ArticleSamplePreview = Article(
    url = URL("https://www.google.com"),
    title = Title("Jetpack Compose app"),
    author = Author("Max Oliinyk"),
    description = Description("Let your imagination fly! Modifiers let you modify your composable " +
            "in a very flexible way. For example, if you wanted to add some outer spacing, change " +
            "the background color of the composable, and round the corners of the Row, you could " +
            "use the following code"),
    published = Date(),
    isFavorite = true,
    urlToImage = URL("https://miro.medium.com/max/4000/1*Ir8CdY5D5Do5R_22Vo3uew.png")
)
