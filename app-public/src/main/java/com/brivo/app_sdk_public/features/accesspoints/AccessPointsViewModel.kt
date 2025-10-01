package com.brivo.app_sdk_public.features.accesspoints

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.app_sdk_public.features.accesspoints.navigation.AccessPointsArgs
import com.brivo.common_app.domain.usecases.GetBrivoSDKLocallyStoredPassesUseCase
import com.brivo.common_app.features.accesspoints.model.AccessPointsUIEvent
import com.brivo.common_app.features.accesspoints.model.AccessPointsViewState
import com.brivo.common_app.features.accesspoints.model.SiteDetailsBottomSheetUIModel
import com.brivo.common_app.features.accesspoints.model.toAccessPointUIModel
import com.brivo.common_app.model.DomainState
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

    private val accessPointArgs: AccessPointsArgs =
        AccessPointsArgs(
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

            is AccessPointsUIEvent.ShouldShowBottomSheet -> {
                _state.update {
                    it.copy(
                        shouldShowBottomSheet = event.shouldShow
                    )
                }
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

                        if (pass == null) {
                            updateAlertMessage("PassId not found in locally stored passes.")
                            return@let
                        }

                        val site = pass.sites.first { site ->
                            site.id == accessPointArgs.siteId
                        }

                        val siteDetailsBottomSheetUIModel = SiteDetailsBottomSheetUIModel(
                            siteId = site.id,
                            siteName = site.siteName,
                            hasTrustedNetwork = site.hasTrustedNetwork,
                            preScreening = site.preScreening,
                            timeZone = site.timeZone
                        )

                        _state.update {
                            it.copy(
                                accessPoints = site.accessPoints.map { accessPoint -> accessPoint.toAccessPointUIModel() },
                                siteName = site.siteName,
                                siteId = site.id,
                                selectedSiteHasTrustedNetwork = site.hasTrustedNetwork,
                                loading = false,
                                siteDetailsBottomSheetUIModel = siteDetailsBottomSheetUIModel
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

    private fun updateAlertMessage(alertMessage: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    alertMessage = alertMessage
                )
            }
        }
    }
}
