package com.me.newsapp.utils.nav_graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.me.newsapp.presentation.web_article.WebArticleScreen
import com.me.newsapp.presentation.home.HomeScreen
import com.me.newsapp.presentation.news_articles.NewsArticlesScreen
import com.me.newsapp.presentation.search.SearchScreen
import com.me.newsapp.presentation.sources.SourcesScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalAnimationApi::class, ExperimentalPagingApi::class)
@Composable
fun RootNavigationGraph(
    navController: NavHostController,
) {
    AnimatedNavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.HOME,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) },
    ) {
        composable(
            route = Graph.HOME,
        ) {
            HomeScreen(onCategoryClick = { category ->
                navController.navigate(Graph.NEWS_SOURCES + "/$category") {
                    launchSingleTop = true
                    restoreState = true
                }
            }, viewModel = hiltViewModel())
        }
        composable(
            route = Graph.NEWS_SOURCES + "/{category}",
            arguments = listOf(navArgument("category") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            category?.let {
                SourcesScreen(onSourcesClick = { sources ->
                    navController.navigate(Graph.NEWS_ARTICLES + "/$sources") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }, viewModel = hiltViewModel())
            }
        }
        composable(
            route = Graph.NEWS_ARTICLES + "/{sources}",
            arguments = listOf(navArgument("sources") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val sources = backStackEntry.arguments?.getString("sources")
            sources?.let {
                NewsArticlesScreen(onNewsArticleClick = { url ->
                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                    navController.navigate(Graph.WEB_NEWS_ARTICLES + "/$encodedUrl") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }, viewModel = hiltViewModel())
            }
        }
        composable(
            route = Graph.WEB_NEWS_ARTICLES + "/{url}",
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            val decodeUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
            decodeUrl?.let {
                WebArticleScreen(it)
            }
        }
        composable(
            route = Graph.SEARCH,
        ) {
            SearchScreen(onNewsArticleClick = { url ->
                val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                navController.navigate(Graph.WEB_NEWS_ARTICLES + "/$encodedUrl") {
                    launchSingleTop = true
                    restoreState = true
                }
            }, viewModel = hiltViewModel())
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val HOME = "home_graph"
    const val NEWS_SOURCES = "news_sources_graph"
    const val NEWS_ARTICLES = "news_articles_graph"
    const val WEB_NEWS_ARTICLES = "web_news_articles_graph"
    const val SEARCH = "search_graph"
}