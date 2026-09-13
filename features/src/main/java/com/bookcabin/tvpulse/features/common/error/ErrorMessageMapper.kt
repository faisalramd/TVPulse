package com.bookcabin.tvpulse.features.common.error

import android.database.sqlite.SQLiteException
import com.bookcabin.tvpulse.features.R
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMessageMapper {

    fun map(throwable: Throwable): ErrorMessage = when (throwable) {
        is HttpException -> fromHttpCode(throwable.code())
        is UnknownHostException -> ErrorMessage(R.string.error_no_internet)
        is SocketTimeoutException -> ErrorMessage(R.string.error_timeout)
        is JsonParseException -> ErrorMessage(R.string.error_invalid_data)
        is IOException -> ErrorMessage(R.string.error_network)
        is SQLiteException -> ErrorMessage(R.string.error_local_data)
        else -> ErrorMessage(R.string.error_generic_message)
    }

    private fun fromHttpCode(code: Int): ErrorMessage = when (code) {
        401, 403 -> ErrorMessage(R.string.error_access_denied)
        404 -> ErrorMessage(R.string.error_not_found)
        408 -> ErrorMessage(R.string.error_timeout)
        429 -> ErrorMessage(R.string.error_too_many_requests)
        in 500..599 -> ErrorMessage(R.string.error_server, listOf(code))
        else -> ErrorMessage(R.string.error_http, listOf(code))
    }
}
