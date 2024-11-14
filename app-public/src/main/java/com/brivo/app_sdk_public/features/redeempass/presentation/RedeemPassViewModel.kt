package com.brivo.app_sdk_public.features.redeempass.presentation

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brivo.app_sdk_public.core.model.DomainState
import com.brivo.app_sdk_public.core.usecase.InitializeBrivoSDKUseCase
import com.brivo.app_sdk_public.features.redeempass.model.RedeemPassUIEvent
import com.brivo.app_sdk_public.features.redeempass.usecase.RedeemMobilePassUseCase
import com.brivo.sdk.enums.ServerRegion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedeemPassViewModel @Inject constructor(
    private val initializeBrivoSDKUseCase: InitializeBrivoSDKUseCase,
    private val redeemMobilePassUseCase: RedeemMobilePassUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RedeemPassViewState())
    val state: StateFlow<RedeemPassViewState> = _state

    fun onEvent(event: RedeemPassUIEvent) {
        when (event) {
            is RedeemPassUIEvent.UpdateEmail -> {
                updateEmail(newValue = event.newValue)
            }
            is RedeemPassUIEvent.UpdateToken -> {
                updateToken(newValue = event.newValue)
            }
            is RedeemPassUIEvent.UpdateRegion -> {
                updateRegion(isRegionUS = event.isRegionUS)
            }
            is RedeemPassUIEvent.UpdateAlertMessage -> {
                updateAlertMessage(alertMessage = event.message)
            }
            is RedeemPassUIEvent.RedeemPass -> {
                redeemPass()
            }
        }
    }

    private fun updateAlertMessage(alertMessage: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    alertMessage = alertMessage,
                    isRedeemingPass = false
                )
            }
        }
    }

    private fun updateMobilePassRedeemed() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    mobilePassRedeemed = true,
                    isRedeemingPass = false
                )
            }
        }
    }

    private fun updateEmail(newValue: TextFieldValue) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    email = newValue
                )
            }
        }
    }

    private fun updateToken(newValue: TextFieldValue) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    token = newValue
                )
            }
        }
    }

    private fun updateRegion(isRegionUS: Boolean) {
        if(isRegionUS){
            initializeBrivoSDKUseCase.execute(
                serverRegion = ServerRegion.UNITED_STATES
            )
        }
        else {
            initializeBrivoSDKUseCase.execute(
                serverRegion = ServerRegion.EUROPE
            )
        }
    }

    private fun redeemPass() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRedeemingPass = true
                )
            }

            when (val result = redeemMobilePassUseCase.execute(
                email = _state.value.email.text.trim(),
                token = _state.value.token.text.trim()
            )) {
                is DomainState.Success -> {
                    updateMobilePassRedeemed()
                }
                is DomainState.Failed -> {
                    updateAlertMessage(result.error)
                }
            }
        }
    }

    data class RedeemPassViewState(
        val email: TextFieldValue = TextFieldValue(""),
        val token: TextFieldValue = TextFieldValue(""),
        val alertMessage: String = "",
        val mobilePassRedeemed: Boolean = false,
        val isRedeemingPass: Boolean = false
    )
}