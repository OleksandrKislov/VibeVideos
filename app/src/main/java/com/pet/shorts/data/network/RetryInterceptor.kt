package com.pet.shorts.data.network

import com.pet.shorts.domain.errors.NetworkError
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

class RetryInterceptor(
    private val maxRetryCount: Int = 3,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var retryCount = 0
        var response: Response

        while (true) {
            val request = chain.request()
            response = chain.proceed(request)

            when (response.code) {
                429 -> {
                    Thread.sleep(500)
                    if(retryCount++ >= maxRetryCount){
                        throw IOException(NetworkError.HttpTooManyRequests())
                    }
                }

                else -> return response
            }
        }
    }
}
