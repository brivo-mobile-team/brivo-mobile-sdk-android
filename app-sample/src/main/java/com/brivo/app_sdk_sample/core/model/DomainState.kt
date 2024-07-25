package com.brivo.app_sdk_sample.core.model

sealed class DomainState<T> {
    data class Success<T>(val data: T) : DomainState<T>()
    data class Failed<T>(val error: String) : DomainState<T>()
}