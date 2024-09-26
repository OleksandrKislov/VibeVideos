package com.pet.shorts.domain.errors

sealed class NetworkError : Exception() {
    class NoInternetConnection : NetworkError()
    class HttpTooManyRequests : NetworkError()
}