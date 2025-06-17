package com.brivo.app_sdk_public.features.accesspoints.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.features.accesspoints.model.AccessPointUIModel
import com.brivo.app_sdk_public.features.accesspoints.model.AccessPointsUIEvent
import com.brivo.app_sdk_public.features.accesspoints.model.toAccessPointUIModel
import com.brivo.app_sdk_public.features.accesspoints.navigation.AccessPointsArgs
import com.brivo.app_sdk_public.features.home.usecase.GetBrivoSDKLocallyStoredPassesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccessPointsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBrivoSDKLocallyStoredPassesUseCase: GetBrivoSDKLocallyStoredPassesUseCase
) : ViewModel() {

    private val accessPointArgs: AccessPointsArgs = AccessPointsArgs(
        savedStateHandle
    )

    private val _state = MutableStateFlow(AccessPointsViewState(passId = accessPointArgs.passId))
    val state: StateFlow<AccessPointsViewState> = _state

    init {
        loadAccessPoints()
    }

    fun onEvent(event: AccessPointsUIEvent) {
        when (event) {
            is AccessPointsUIEvent.UpdateAlertMessage -> {
                updateAlertMessage(alertMessage = event.message)
            }
        }
    }

    private fun loadAccessPoints() {
        viewModelScope.launch {
            when (val result = getBrivoSDKLocallyStoredPassesUseCase.execute()) {
                is DomainState.Success -> {
                    result.data?.let { data ->
                        val pass = data.entries.firstOrNull { entry ->
                            entry.key == accessPointArgs.passId
                        }?.value

                        pass?.let { passValue ->
                            val site = passValue.sites.firstOrNull { site ->
                                site.id == accessPointArgs.siteId
                            }

                            site?.let { siteValue ->
                                _state.update {
                                    it.copy(
                                        accessPoints = siteValue.accessPoints.map { accessPoint -> accessPoint.toAccessPointUIModel() },
                                        siteName = siteValue.siteName,
                                        selectedSiteHasTrustedNetwork = site.hasTrustedNetwork,
                                        loading = false
                                    )
                                }
                            } ?: run {
                                _state.update {
                                    it.copy(loading = false)
                                }
                            }
                        } ?: run {
                            _state.update {
                                it.copy(loading = false)
                            }
                        }
                    }

                }
                is DomainState.Failed -> {
                    updateAlertMessage(result.error)
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

    data class AccessPointsViewState(
        val passId: String = "",
        val siteName: String = "",
        val accessPoints: List<AccessPointUIModel> = emptyList(),
        val selectedSiteHasTrustedNetwork: Boolean = false,
        val loading: Boolean = true,
        val alertMessage: String = ""
    )
}
