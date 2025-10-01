package com.brivo.app_sdk_public.features.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.app_sdk_public.BrivoSampleConstants
import com.brivo.app_sdk_public.features.home.model.HomeUIEvent
import com.brivo.common_app.domain.usecases.GetBrivoSDKLocallyStoredPassesUseCase
import com.brivo.common_app.domain.usecases.GetBrivoSDKVersionUseCase
import com.brivo.common_app.domain.usecases.RefreshPassesUseCase
import com.brivo.common_app.features.home.model.BrivoOnairPassUIModel
import com.brivo.common_app.features.home.model.PassDetailsBottomSheetUIModel
import com.brivo.common_app.features.home.model.toBrivoOnairPassUIModel
import com.brivo.common_app.features.home.usecase.RefreshAllegionCredentialsUseCase
import com.brivo.common_app.model.DomainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBrivoSDKVersionUseCase: GetBrivoSDKVersionUseCase,
    private val getBrivoSDKLocallyStoredPassesUseCase: GetBrivoSDKLocallyStoredPassesUseCase,
    private val refreshPassesUseCase: RefreshPassesUseCase,
    private val refreshAllegionCredentialsUseCase: RefreshAllegionCredentialsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState> = _state

    init {
        getMobileSDKVersion()
    }

    fun onEvent(event: HomeUIEvent) {
        when (event) {
            is HomeUIEvent.LoadPasses -> {
                loadPasses()
            }

            is HomeUIEvent.RefreshPasses -> {
                refreshPasses()
            }

            is HomeUIEvent.UpdateAlertMessage -> {
                updateAlertMessage(alertMessage = event.message)
            }

            is HomeUIEvent.ShouldShowBotomSheet -> {
                _state.update {
                    val passDetailsBottomSheetUIModel = PassDetailsBottomSheetUIModel(
                        shouldShowBottomSheet = event.shouldShow
                    )
                    it.copy(
                        passDetailsBottomSheetUIModel = passDetailsBottomSheetUIModel
                    )
                }
            }

            is HomeUIEvent.UpdateBottomSheetInformation -> {
                _state.update {
                    val passDetailsBottomSheetUIModel = PassDetailsBottomSheetUIModel(
                        hasAllegionBleCredentials = event.hasAllegionBleCredentials,
                        hasHidOrigoMobilePass = event.hasHidOrigoMobilePass,
                        hidOrigoWalletPassEnabled = event.hidOrigoWalletPassEnabled,
                        hasBrivoWalletPass = event.hasBrivoWalletPass
                    )
                    it.copy(
                        passDetailsBottomSheetUIModel = passDetailsBottomSheetUIModel
                    )
                }
            }
        }
    }

    private fun getMobileSDKVersion() {
        viewModelScope.launch {
            val version = getBrivoSDKVersionUseCase.execute()
            _state.update {
                it.copy(
                    version = version
                )
            }
        }
    }

    private fun refreshAllegionCredentials() {
        viewModelScope.launch {
            when (refreshAllegionCredentialsUseCase.execute()) {
                is DomainState.Failed -> {
                    Log.d("HomeViewModel", "Failed to refresh allegion credentials")
                }

                is DomainState.Success -> {
                    Log.d("HomeViewModel", "Success refresh allegion credentials")
                }
            }
        }
    }

    private fun loadPasses() {
        viewModelScope.launch {
            when (val result = getBrivoSDKLocallyStoredPassesUseCase.execute()) {
                is DomainState.Success -> {
                    result.data?.let { data ->
                        _state.update {
                            it.copy(
                                passes = data.entries.map { entry ->
                                    entry.value.toBrivoOnairPassUIModel()
                                },
                                loading = false,
                                refreshing = false
                            )
                        }
                    }
                }

                is DomainState.Failed -> {
                    updateAlertMessage(result.error)
                }
            }
        }
    }

    private fun refreshPasses() {
        viewModelScope.launch {
            if (_state.value.passes.isEmpty()) {
                return@launch
            }
            _state.update {
                it.copy(
                    refreshing = true,
                    loading = true
                )
            }
            _state.value.passes.forEach {
                when (val result = refreshPassesUseCase.execute(
                    refreshToken = it.refreshToken,
                    accessToken = it.accessToken
                )) {
                    is DomainState.Success -> {
                        loadPasses()
                        refreshAllegionCredentials()
                    }

                    is DomainState.Failed -> {
                        var alertMessage = "Something went wrong, please try again!"

                        when (result.errorCode) {
                            BrivoSampleConstants.ERROR_CODE_USER_SUSPENDED -> {
                                _state.update { currentState ->
                                    currentState.copy(
                                        refreshing = false,
                                        loading = false,
                                        passes = listOf()
                                    )
                                }
                                alertMessage = "User suspended"
                            }
                            BrivoSampleConstants.ERROR_CODE_AUTHENTICATION_EXCEPTION -> {
                                _state.update { currentState ->
                                    currentState.copy(
                                        refreshing = false,
                                        loading = false,
                                        passes = listOf()
                                    )
                                }
                                alertMessage = "Authentication exception"
                            }
                            else -> {
                                _state.update { currentState ->
                                    currentState.copy(
                                        refreshing = false,
                                        loading = false
                                    )
                                }
                            }
                        }
                        updateAlertMessage(alertMessage = alertMessage)
                    }
                }
            }
        }
    }

    private fun updateAlertMessage(alertMessage: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    alertMessage = alertMessage
                )
            }
        }
    }

    data class HomeViewState(
        val passes: List<BrivoOnairPassUIModel> = emptyList(),
        val version: String = "",
        val alertMessage: String = "",
        val loading: Boolean = true,
        val refreshing: Boolean = false,
        val passDetailsBottomSheetUIModel: PassDetailsBottomSheetUIModel = PassDetailsBottomSheetUIModel()
    )
}
