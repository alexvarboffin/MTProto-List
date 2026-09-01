package com.walhalla.mtprotocompose.viewmodel



import android.app.Application

import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope

import com.walhalla.mtproto.shared.repository.ProxyRepository

import com.walhalla.mtprotolist.entity.MtprotoProxy

import com.walhalla.mtprotocompose.data.FirebaseProxyStreams

import com.walhalla.mtprotocompose.mtprotoApp

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch



data class MtprotoListUiState(

    val isLoading: Boolean = false,

    val items: List<MtprotoProxy> = emptyList(),

    val error: String? = null,

    val successMessage: String? = null,

)



class MtprotoListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProxyRepository = application.mtprotoApp().repository



    private val _uiState = MutableStateFlow(MtprotoListUiState(isLoading = true))

    val uiState: StateFlow<MtprotoListUiState> = _uiState.asStateFlow()



    init {

        viewModelScope.launch {

            FirebaseProxyStreams.mtprotoList().collect { result ->

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

            repository.loadMtprotoList()

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


