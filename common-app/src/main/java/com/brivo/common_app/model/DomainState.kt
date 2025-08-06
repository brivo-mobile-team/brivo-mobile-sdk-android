package com.brivo.common_app.model

sealed class DomainState<T> {
    data class Success<T>(val data: T) : DomainState<T>()
    data class Failed<T>(
        val error: String,
        val errorCode: Int = -1
    ) : DomainState<T>()
}
