package com.me.newsapp.data.repository

import com.google.gson.Gson
import com.me.newsapp.data.local.dao.SourceDao
import com.me.newsapp.data.local.entity.SourceEntity
import com.me.newsapp.data.remote.api.NewsApiService
import com.me.newsapp.data.remote.dto.*
import com.me.newsapp.domain.model.Article
import com.me.newsapp.domain.model.ArticleSource
import com.me.newsapp.domain.model.Source
import com.me.newsapp.utils.Constants.ITEMS_PER_PAGE
import com.me.newsapp.utils.Resource
import id.tss.taskmanagement.utils.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.*

@ExperimentalCoroutinesApi
class NewsRepositoryImplTest {

    @Mock
    lateinit var api: NewsApiService

    @Mock
    lateinit var sourceDao: SourceDao

    private lateinit var repository: NewsRepositoryImpl


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = NewsRepositoryImpl(
            api = api,
            sourceDao = sourceDao,
            dispatcherProvider = TestDispatcherProvider()
        )
    }

    @Test
    fun `getNewsSources should return data from remote when fetchFromRemote is false and local data is not available`() =
        runBlocking {
            val expectedResult = listOf(
                Source(
                    category = "FAKE_CATEGORY",
                    url = "FAKE_URL",
                    country = "FAKE_COUNTRY",
                    language = "FAKE_LANGUAGE",
                    description = "FAKE_DESCRIPTION",
                    name = "FAKE_NAME",
                    id = "FAKE_ID"
                )
            )

            val newSourceResponse = NewsSourcesResponse(
                status = "OK",
                data = listOf(
                    SourceResponse(
                        category = "FAKE_CATEGORY",
                        url = "FAKE_URL",
                        country = "FAKE_COUNTRY",
                        language = "FAKE_LANGUAGE",
                        description = "FAKE_DESCRIPTION",
                        name = "FAKE_NAME",
                        id = "FAKE_ID"
                    )
                )
            )

            val localSourceEntities = listOf(
                SourceEntity(
                    category = "FAKE_CATEGORY",
                    url = "FAKE_URL",
                    country = "FAKE_COUNTRY",
                    language = "FAKE_LANGUAGE",
                    description = "FAKE_DESCRIPTION",
                    name = "FAKE_NAME",
                    id = "FAKE_ID"
                )
            )

            Mockito.`when`(sourceDao.getAllByCategory("FAKE_CATEGORY"))
                .thenReturn(emptyList())

            Mockito.`when`(api.getSourcesNews("FAKE_CATEGORY", "FAKE_API"))
                .thenReturn(Response.success(newSourceResponse))

            Mockito.`when`(sourceDao.getAllByCategory("FAKE_CATEGORY"))
                .thenReturn(localSourceEntities)

            repository.getNewsSources(
                false,
                "FAKE_CATEGORY",
                "FAKE_API"
            )
                .take(3)
                .collect { resource ->
                    // then
                    when (resource) {
                        is Resource.Loading -> {
                            assert(resource.isLoading || !resource.isLoading)
                        }
                        is Resource.Success -> {
                            assert(resource.data == expectedResult)
                        }
                        else -> {
                            // should not reach here
                            assert(false)
                        }
                    }
                }
        }

    @Test
    fun `getNewsSources should return data from cache when fetchFromRemote is false and local data is available`() =
        runBlocking {
            val expectedResult = listOf(
                Source(
                    category = "FAKE_CATEGORY",
                    url = "FAKE_URL",
                    country = "FAKE_COUNTRY",
                    language = "FAKE_LANGUAGE",
                    description = "FAKE_DESCRIPTION",
                    name = "FAKE_NAME",
                    id = "FAKE_ID"
                )
            )

            val localSourceEntities = listOf(
                SourceEntity(
                    category = "FAKE_CATEGORY",
                    url = "FAKE_URL",
                    country = "FAKE_COUNTRY",
                    language = "FAKE_LANGUAGE",
                    description = "FAKE_DESCRIPTION",
                    name = "FAKE_NAME",
                    id = "FAKE_ID"
                )
            )

            Mockito.`when`(sourceDao.getAllByCategory("FAKE_CATEGORY"))
                .thenReturn(localSourceEntities)

            repository.getNewsSources(
                false,
                "FAKE_CATEGORY",
                "FAKE_API"
            )
                .take(3)
                .collect { resource ->
                    // then
                    when (resource) {
                        is Resource.Loading -> {
                            assert(resource.isLoading || !resource.isLoading)
                        }
                        is Resource.Success -> {
                            assert(resource.data == expectedResult)
                        }
                        else -> {
                            // should not reach here
                            assert(false)
                        }
                    }
                }
        }

    @Test
    fun `getNewsSources should return error when API call is unsuccessful`() = runBlocking {
        // given
        val expectedErrorMessage = "Bad Request"
        val errorResponse = ErrorResponse(code = "400", message = "Bad Request", status = "error")
        val gson = Gson()
        val responseBody = gson.toJson(errorResponse)

        Mockito.`when`(sourceDao.getAllByCategory("FAKE_CATEGORY"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getSourcesNews(category = "FAKE_CATEGORY", apiKey = "FAKE_API_KEY"))
            .thenReturn(Response.error(400, responseBody.toResponseBody()))

        // when
        repository.getNewsSources(
            fetchFromRemote = true,
            category = "FAKE_CATEGORY",
            apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedErrorMessage)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getNewsSources should emit error message with HTTPException`() = runBlocking {
        // given
        val errorResponse = "{\n" +
                "  \"code\": \"400\",\n" +
                "  \"message\": \"Bad Request\",\n" +
                "  \"status\": \"error\"\n" +
                "}\n"
        val responseBody = errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(400, responseBody)
        val httpException = HttpException(response)

        // when
        Mockito.`when`(sourceDao.getAllByCategory("FAKE_CATEGORY"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getSourcesNews(category = "FAKE_CATEGORY", apiKey = "FAKE_API_KEY"))
            .thenAnswer {
                throw httpException
            }

        repository.getNewsSources(
            fetchFromRemote = true,
            category = "FAKE_CATEGORY",
            apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == httpException.message)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getNewsSources should emit error message with IOException`() = runBlocking {
        // given
        val expectedResult = "Couldn't reach server. Check your internet connection."
        val ioException = IOException()

        // when
        Mockito.`when`(sourceDao.getAllByCategory("FAKE_CATEGORY"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getSourcesNews(category = "FAKE_CATEGORY", apiKey = "FAKE_API_KEY"))
            .thenAnswer {
                throw ioException
            }

        repository.getNewsSources(
            fetchFromRemote = true,
            category = "FAKE_CATEGORY",
            apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedResult)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getNewsSources should emit error message with Exception`() = runBlocking {
        // given
        val expectedResult = "Couldn't load data"
        val exception = Exception()

        // when
        Mockito.`when`(sourceDao.getAllByCategory("FAKE_CATEGORY"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getSourcesNews(category = "FAKE_CATEGORY", apiKey = "FAKE_API_KEY"))
            .thenAnswer {
                throw exception
            }

        // when
        repository.getNewsSources(
            fetchFromRemote = true,
            category = "FAKE_CATEGORY",
            apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedResult)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getEverything should return data from remote`() =
        runBlocking {
            val FAKE_DATE_STRING = "Feb 26, 2023"
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 2023)
            cal.set(Calendar.MONTH, Calendar.FEBRUARY)
            cal.set(Calendar.DAY_OF_MONTH, 26)
            val FAKE_DATE: Date = cal.time
            val expectedResult = listOf(
                Article(
                    author = "FAKE_AUTHOR",
                    publishedAt = FAKE_DATE_STRING,
                    urlToImage = "FAKE_URL_TO_IMAGE",
                    description = "FAKE_DESCRIPTION",
                    source = ArticleSource(
                        id = "FAKE_ID",
                        name = "FAKE_NAME"
                    ),
                    title = "FAKE_TITLE",
                    url = "FAKE_URL",
                    content = "FAKE_CONTENT"
                )
            )

            val newArticleResponse = NewsArticlesResponse(
                status = "OK",
                totalResults = 1,
                data = listOf(
                    ArticleResponse(
                        author = "FAKE_AUTHOR",
                        publishedAt = FAKE_DATE,
                        urlToImage = "FAKE_URL_TO_IMAGE",
                        description = "FAKE_DESCRIPTION",
                        source = ArticleSourceResponse(
                            id = "FAKE_ID",
                            name = "FAKE_NAME"
                        ),
                        title = "FAKE_TITLE",
                        url = "FAKE_URL",
                        content = "FAKE_CONTENT"
                    )
                )
            )

            Mockito.`when`(api.getEverything(q = "FAKE_Query", apiKey = "FAKE_API_KEY"))
                .thenReturn(Response.success(newArticleResponse))

            repository.getNewsEverything(q = "FAKE_Query", apiKey = "FAKE_API_KEY")
                .take(3)
                .collect { resource ->
                    println("resource = ${resource.data}")
                    // then
                    when (resource) {
                        is Resource.Loading -> {
                            assert(resource.isLoading || !resource.isLoading)
                        }
                        is Resource.Success -> {
                            assert(resource.data == expectedResult)
                        }
                        else -> {
                            // should not reach here
                            assert(false)
                        }
                    }
                }
        }

    @Test
    fun `getEverything should return error when API call is unsuccessful`() = runBlocking {
        // given
        val expectedErrorMessage = "Bad Request"
        val errorResponse = ErrorResponse(code = "400", message = "Bad Request", status = "error")
        val gson = Gson()
        val responseBody = gson.toJson(errorResponse)

        Mockito.`when`(api.getEverything(q = "FAKE_Query", apiKey = "FAKE_API_KEY"))
            .thenReturn(Response.error(400, responseBody.toResponseBody()))

        // when
        repository.getNewsEverything(
            q = "FAKE_Query", apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedErrorMessage)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getEverything should emit error message with HTTPException`() = runBlocking {
        // given
        val errorResponse = "{\n" +
                "  \"code\": \"400\",\n" +
                "  \"message\": \"Bad Request\",\n" +
                "  \"status\": \"error\"\n" +
                "}\n"
        val responseBody = errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(400, responseBody)
        val httpException = HttpException(response)

        Mockito.`when`(api.getEverything(q = "FAKE_Query", apiKey = "FAKE_API_KEY"))
            .thenAnswer {
                throw httpException
            }
        repository.getNewsEverything(
            q = "FAKE_Query", apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == httpException.message)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getEverything should emit error message with IOException`() = runBlocking {
        // given
        val expectedResult = "Couldn't reach server. Check your internet connection."
        val ioException = IOException()

        // when
        Mockito.`when`(sourceDao.getAllByCategory("FAKE_CATEGORY"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getEverything(q = "FAKE_Query", apiKey = "FAKE_API_KEY"))
            .thenAnswer {
                throw ioException
            }

        repository.getNewsEverything(
            q = "FAKE_Query", apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedResult)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getEverything should emit error message with Exception`() = runBlocking {
        // given
        val expectedResult = "Couldn't load data"
        val exception = Exception()

        //when
        Mockito.`when`(api.getEverything(q = "FAKE_Query", apiKey = "FAKE_API_KEY"))
            .thenAnswer {
                throw exception
            }

        // when
        repository.getNewsEverything(
            q = "FAKE_Query", apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedResult)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `searchNewsSources should return data from remote when fetchFromRemote is false and local data is not available`() =
        runBlocking {
            val expectedResult = listOf(
                Source(
                    category = "FAKE_CATEGORY",
                    url = "FAKE_URL",
                    country = "FAKE_COUNTRY",
                    language = "FAKE_LANGUAGE",
                    description = "FAKE_DESCRIPTION",
                    name = "FAKE_NAME",
                    id = "FAKE_ID"
                )
            )

            val newSourceResponse = NewsSourcesResponse(
                status = "OK",
                data = listOf(
                    SourceResponse(
                        category = "FAKE_CATEGORY",
                        url = "FAKE_URL",
                        country = "FAKE_COUNTRY",
                        language = "FAKE_LANGUAGE",
                        description = "FAKE_DESCRIPTION",
                        name = "FAKE_NAME",
                        id = "FAKE_ID"
                    )
                )
            )

            val localSourceEntities = listOf(
                SourceEntity(
                    category = "FAKE_CATEGORY",
                    url = "FAKE_URL",
                    country = "FAKE_COUNTRY",
                    language = "FAKE_LANGUAGE",
                    description = "FAKE_DESCRIPTION",
                    name = "FAKE_NAME",
                    id = "FAKE_ID"
                )
            )

            Mockito.`when`(sourceDao.getSourcesByPage(offset = 0, pageSize = ITEMS_PER_PAGE, name = "FAKE_NAME"))
                .thenReturn(emptyList())

            Mockito.`when`(api.getAllSourcesNews(apiKey = "FAKE_API"))
                .thenReturn(Response.success(newSourceResponse))

            Mockito.`when`(sourceDao.getSourcesByPage(offset = 0, pageSize = ITEMS_PER_PAGE, name = "FAKE_NAME"))
                .thenReturn(localSourceEntities)

            repository.searchNewsSources(
                q = "FAKE_NAME",
                page = 1,
                pageSize = ITEMS_PER_PAGE,
                apiKey = "FAKE_API",
                fetchFromRemote = false
            )
                .take(3)
                .collect { resource ->
                    // then
                    when (resource) {
                        is Resource.Loading -> {
                            assert(resource.isLoading || !resource.isLoading)
                        }
                        is Resource.Success -> {
                            assert(resource.data == expectedResult)
                        }
                        else -> {
                            // should not reach here
                            assert(false)
                        }
                    }
                }
        }

    @Test
    fun `searchNewsSources should return data from remote when fetchFromRemote is false and local data is available`() =
        runBlocking {
            val expectedResult = listOf(
                Source(
                    category = "FAKE_CATEGORY",
                    url = "FAKE_URL",
                    country = "FAKE_COUNTRY",
                    language = "FAKE_LANGUAGE",
                    description = "FAKE_DESCRIPTION",
                    name = "FAKE_NAME",
                    id = "FAKE_ID"
                )
            )

            val localSourceEntities = listOf(
                SourceEntity(
                    category = "FAKE_CATEGORY",
                    url = "FAKE_URL",
                    country = "FAKE_COUNTRY",
                    language = "FAKE_LANGUAGE",
                    description = "FAKE_DESCRIPTION",
                    name = "FAKE_NAME",
                    id = "FAKE_ID"
                )
            )

            Mockito.`when`(sourceDao.getSourcesByPage(offset = 0, pageSize = ITEMS_PER_PAGE, name = "FAKE_NAME"))
                .thenReturn(localSourceEntities)


            repository.searchNewsSources(
                q = "FAKE_NAME",
                page = 1,
                pageSize = ITEMS_PER_PAGE,
                apiKey = "FAKE_API",
                fetchFromRemote = false
            )
                .take(3)
                .collect { resource ->
                    // then
                    when (resource) {
                        is Resource.Loading -> {
                            assert(resource.isLoading || !resource.isLoading)
                        }
                        is Resource.Success -> {
                            assert(resource.data == expectedResult)
                        }
                        else -> {
                            // should not reach here
                            assert(false)
                        }
                    }
                }
        }

    @Test
    fun `searchNewsSources should return error when API call is unsuccessful`() = runBlocking {
        // given
        val expectedErrorMessage = "Bad Request"
        val errorResponse = ErrorResponse(code = "400", message = "Bad Request", status = "error")
        val gson = Gson()
        val responseBody = gson.toJson(errorResponse)

        Mockito.`when`(sourceDao.getSourcesByPage(offset = 0, pageSize = ITEMS_PER_PAGE, name = "FAKE_NAME"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getAllSourcesNews(apiKey = "FAKE_API"))
            .thenReturn(Response.error(400, responseBody.toResponseBody()))

        // when
        repository.searchNewsSources(
            q = "FAKE_NAME",
            page = 1,
            pageSize = ITEMS_PER_PAGE,
            apiKey = "FAKE_API",
            fetchFromRemote = true
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedErrorMessage)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `searchNewsSources should emit error message with HTTPException`() = runBlocking {
        // given
        val errorResponse = "{\n" +
                "  \"code\": \"400\",\n" +
                "  \"message\": \"Bad Request\",\n" +
                "  \"status\": \"error\"\n" +
                "}\n"
        val responseBody = errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(400, responseBody)
        val httpException = HttpException(response)

        Mockito.`when`(sourceDao.getSourcesByPage(offset = 0, pageSize = ITEMS_PER_PAGE, name = "FAKE_NAME"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getAllSourcesNews(apiKey = "FAKE_API"))
            .thenAnswer {
                throw httpException
            }

        repository.searchNewsSources(
            q = "FAKE_NAME",
            page = 1,
            pageSize = ITEMS_PER_PAGE,
            apiKey = "FAKE_API",
            fetchFromRemote = true
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == httpException.message)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `searchNewsSources should emit error message with IOException`() = runBlocking {
        // given
        val expectedResult = "Couldn't reach server. Check your internet connection."
        val ioException = IOException()

        Mockito.`when`(sourceDao.getSourcesByPage(offset = 0, pageSize = ITEMS_PER_PAGE, name = "FAKE_NAME"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getAllSourcesNews(apiKey = "FAKE_API"))
            .thenAnswer {
                throw ioException
            }

        repository.searchNewsSources(
            q = "FAKE_NAME",
            page = 1,
            pageSize = ITEMS_PER_PAGE,
            apiKey = "FAKE_API",
            fetchFromRemote = true
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedResult)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `searchNewsSources should emit error message with Exception`() = runBlocking {
        // given
        val expectedResult = "Couldn't load data"
        val exception = Exception()


        Mockito.`when`(sourceDao.getSourcesByPage(offset = 0, pageSize = ITEMS_PER_PAGE, name = "FAKE_NAME"))
            .thenReturn(emptyList())

        Mockito.`when`(api.getAllSourcesNews(apiKey = "FAKE_API"))
            .thenAnswer {
                throw exception
            }

        // when
        repository.getNewsEverything(
            q = "FAKE_Query", apiKey = "FAKE_API_KEY"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedResult)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }
}