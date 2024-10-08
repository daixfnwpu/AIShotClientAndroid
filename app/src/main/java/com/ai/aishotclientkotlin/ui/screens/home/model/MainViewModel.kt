package com.ai.aishotclientkotlin.ui.screens.home.model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.ai.aishotclientkotlin.data.repository.DiscoverRepository
import com.ai.aishotclientkotlin.data.dao.entity.Movie
import com.ai.aishotclientkotlin.domain.model.bi.network.NetworkState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
   // val imageLoader: ImageLoader,
    private val discoverRepository: DiscoverRepository
) : ViewModel() {


    private val _movieLoadingState: MutableState<NetworkState> = mutableStateOf(NetworkState.IDLE)
    val movieLoadingState: State<NetworkState> get() = _movieLoadingState

    val movies: State<MutableList<Movie>> = mutableStateOf(mutableListOf())
    val moviePageStateFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    private val newMovieFlow = moviePageStateFlow.flatMapLatest {
        _movieLoadingState.value = NetworkState.LOADING
        discoverRepository.loadMovies(
            page = it,
            success = { _movieLoadingState.value = NetworkState.SUCCESS },
            error = { _movieLoadingState.value = NetworkState.ERROR
                    Log.e("discoverRepository","discoverRepository.loadMovies error")}
        )
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    private val _shopLoadingState: MutableState<NetworkState> = mutableStateOf(NetworkState.IDLE)
    val shopLoadingState: State<NetworkState> get() = _shopLoadingState


    init {
        viewModelScope.launch(Dispatchers.IO) {
            newMovieFlow.collectLatest {
                movies.value.addAll(it)
            }
        }

    }

    fun fetchNextMoviePage() {
        if (movieLoadingState.value != NetworkState.LOADING) {
            moviePageStateFlow.value++
        }
    }



}
