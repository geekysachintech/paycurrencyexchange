package com.example.paycurrencyexchange.data.remote

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class ApiResponse<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): ApiResponse<T> = ApiResponse(Status.SUCCESS, data, null)
        fun <T> error(message: String, data: T? = null): ApiResponse<T> = ApiResponse(Status.ERROR, data, message)
        fun <T> loading(data: T? = null): ApiResponse<T> = ApiResponse(Status.LOADING, data, null)
    }
}