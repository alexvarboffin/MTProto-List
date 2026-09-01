package com.walhalla.mtprotocompose.viewmodel



import android.app.Application

import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope

import com.walhalla.mtproto.shared.repository.ProxyRepository

import com.walhalla.mtprotolist.webproxy.ProxyInfo

import com.walhalla.mtprotocompose.data.FirebaseProxyStreams

import com.walhalla.mtprotocompose.mtprotoApp

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch



data class WebProxyListUiState(

    val isLoading: Boolean = false,

    val items: List<ProxyInfo> = emptyList(),

    val error: String? = null,

    val successMessage: String? = null,

)



class WebProxyListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProxyRepository = application.mtprotoApp().repository



    private val _uiState = MutableStateFlow(WebProxyListUiState(isLoading = true))

    val uiState: StateFlow<WebProxyListUiState> = _uiState.asStateFlow()



    init {

        viewModelScope.launch {

            FirebaseProxyStreams.webProxyList().collect { result ->

                result.onSuccess { list ->

                    _uiState.update {

                        it.copy(

                            isLoading = false,

                            items = list,

                            error = null,

                            successMessage = SUCCESS_MESSAGE,

                        )

                    }

                }.onFailure { error ->

                    _uiState.update {

                        it.copy(

                            isLoading = false,

                            error = error.message ?: "Load failed",

                        )

                    }

                }

            }

        }

    }



    fun refresh() {

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.loadWebProxyList()

                .onSuccess { list ->

                    _uiState.update {

                        it.copy(

                            isLoading = false,

                            items = list,

                            error = null,

                            successMessage = SUCCESS_MESSAGE,

                        )

                    }

                }

                .onFailure { error ->

                    _uiState.update {

                        it.copy(isLoading = false, error = error.message ?: "Load failed")

                    }

                }

        }

    }



    fun consumeSuccessMessage() {

        _uiState.update { it.copy(successMessage = null) }

    }



    companion object {

        const val SUCCESS_MESSAGE = "data_successfully_updated"

    }

}


