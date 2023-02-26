package com.me.newsapp.presentation.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.me.newsapp.ui.theme.NewsAppTheme
import com.me.newsapp.utils.nav_graph.Graph

@Composable
fun TopBar(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topBarDestination =
        TopBarDestination.values().any { it.route == currentDestination?.route }

    if (topBarDestination) {
        NewsAppTheme {
            when (currentDestination?.route) {
                TopBarDestination.SEARCH.route -> {
                    DefaultTopBar(title = TopBarDestination.SEARCH.label,
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                            }
                        })
                }
                TopBarDestination.Home.route -> {
                    DefaultTopBar(title = TopBarDestination.Home.label,
                        actions = {
                            IconButton(onClick = {
                                navController.navigate(Graph.SEARCH) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(Icons.Filled.Search, null, tint = Color.White)
                            }
                        })
                }
                TopBarDestination.NEWS_SOURCES.route -> {
                    DefaultTopBar(title = TopBarDestination.NEWS_SOURCES.label,
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                            }
                        }, actions = {
                            IconButton(onClick = {
                                navController.navigate(Graph.SEARCH) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(Icons.Filled.Search, null, tint = Color.White)
                            }
                        })
                }
                TopBarDestination.NEWS_ARTICLES.route -> {
                    DefaultTopBar(title = TopBarDestination.NEWS_ARTICLES.label,
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                navController.navigate(Graph.SEARCH) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(Icons.Filled.Search, null, tint = Color.White)
                            }
                        })
                }
                TopBarDestination.WEB_NEWS_ARTICLES.route -> {
                    DefaultTopBar(title = TopBarDestination.WEB_NEWS_ARTICLES.label,
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                navController.navigate(Graph.SEARCH) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(Icons.Filled.Search, null, tint = Color.White)
                            }
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun DefaultTopBar(
    title: String, navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        elevation = 0.dp,
        title = {
            Text(
                text = title.uppercase(),
                style = TextStyle.Default.copy(
                    drawStyle = Stroke(
                        miter = 10f,
                        width = 5f,
                        join = StrokeJoin.Round
                    )
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

        },
        navigationIcon = navigationIcon,
        actions = actions
    )
}


enum class TopBarDestination(
    val route: String,
    val label: String,
) {
    Home(Graph.HOME, "News"),
    NEWS_SOURCES(Graph.NEWS_SOURCES + "/{category}", "News Sources"),
    NEWS_ARTICLES(Graph.NEWS_ARTICLES + "/{sources}", "News Articles"),
    WEB_NEWS_ARTICLES(Graph.WEB_NEWS_ARTICLES + "/{url}", "Web News"),
    SEARCH(Graph.SEARCH, "Search News"),
}
