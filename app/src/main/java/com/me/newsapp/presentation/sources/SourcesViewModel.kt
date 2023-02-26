package com.me.newsapp.presentation.sources

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.me.newsapp.BuildConfig
import com.me.newsapp.domain.use_case.GetNewsSourcesUseCase
import com.me.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class SourcesViewModel @Inject constructor(
    private val getNewsSourcesUseCase: GetNewsSourcesUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val category: String = checkNotNull(savedStateHandle["category"])

    var state by mutableStateOf(SourcesState())

    private val _event = MutableSharedFlow<SourcesEvent>()
    val event: SharedFlow<SourcesEvent> get() = _event

    init {
        doGetNewsSources(category = category)
    }

    fun doRefresh() {
        doGetNewsSources(fetchFromRemote = true, category = category)
    }

    private fun doGetNewsSources(
        fetchFromRemote: Boolean = false,
        category: String,
    ) {
        viewModelScope.launch {
            getNewsSourcesUseCase
                .execute(
                    fetchFromRemote = fetchFromRemote,
                    category = category,
                    apiKey = BuildConfig.API_KEY
                )
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            resource.isLoading.let { isLoading ->
                                state = state.copy(
                                    isLoading = isLoading
                                )
                            }
                        }
                        is Resource.Success -> {
                            resource.data?.let { data ->
                                state = state.copy(
                                    sources = data
                                )
                            }
                        }
                        is Resource.Error -> {
                            resource.message?.let { message ->
                                _event.emit(SourcesEvent.ShowToast(message))
                            }
                        }
                    }
                }
        }
    }
}