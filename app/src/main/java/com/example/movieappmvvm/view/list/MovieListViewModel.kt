package com.example.movieappmvvm.view.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieappmvvm.data.Movie
import com.example.movieappmvvm.data.repository.MovieRemoteRepository
import com.example.movieappmvvm.util.Debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(private val movieRemoteRepository: MovieRemoteRepository) : ViewModel() {
    var movieList = MutableLiveData<ArrayList<Movie>>()
    var page = 1
    var isLoadDone = true
    var isSearch = false

    init {
        getMovieList(1)
    }

    fun getMovieList(page: Int) {
        viewModelScope.launch {
            val response = movieRemoteRepository.getMovieList(page)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Debug.log("get movie : ${response.body()}")
                    val list = response.body()?.data?.movies
                    val prevList = movieList.value

                    if (!prevList.isNullOrEmpty()) {
                        list?.let {
                            for (i in it.iterator()) {
                                prevList.add(i)
                            }
                        }
                        movieList.value = prevList
                    } else {
                        movieList.value = list
                    }
                    isLoadDone = true
                    isSearch = false
                } else {
                    Debug.log("get movie error : ${response.message()}")
                }
            }
        }
    }

    fun searchMovies(query: String?) {
        query?.let {
            viewModelScope.launch {
                val response = movieRemoteRepository.searchMovieList(query)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Debug.log("search movie : ${response.body()}")
                        if (response.body()?.data?.movies?.get(0) != null) {
                            movieList.value = response.body()?.data?.movies
                            isSearch = true
                        }

                    } else {
                        Debug.log("search movie error : ${response.message()}")
                    }
                }
            }
        }
    }

}