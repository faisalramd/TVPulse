package com.bookcabin.tvpulse.features.common.error

import com.bookcabin.tvpulse.features.R
import com.google.gson.JsonSyntaxException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMessageMapperTest {

    private fun httpException(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    @Test
    fun `maps http status codes`() {
        assertEquals(ErrorMessage(R.string.error_access_denied), ErrorMessageMapper.map(httpException(401)))
        assertEquals(ErrorMessage(R.string.error_access_denied), ErrorMessageMapper.map(httpException(403)))
        assertEquals(ErrorMessage(R.string.error_not_found), ErrorMessageMapper.map(httpException(404)))
        assertEquals(ErrorMessage(R.string.error_timeout), ErrorMessageMapper.map(httpException(408)))
        assertEquals(ErrorMessage(R.string.error_too_many_requests), ErrorMessageMapper.map(httpException(429)))
        assertEquals(ErrorMessage(R.string.error_server, listOf(503)), ErrorMessageMapper.map(httpException(503)))
        assertEquals(ErrorMessage(R.string.error_http, listOf(400)), ErrorMessageMapper.map(httpException(400)))
    }

    @Test
    fun `maps network exceptions`() {
        assertEquals(ErrorMessage(R.string.error_no_internet), ErrorMessageMapper.map(UnknownHostException()))
        assertEquals(ErrorMessage(R.string.error_timeout), ErrorMessageMapper.map(SocketTimeoutException()))
        assertEquals(ErrorMessage(R.string.error_network), ErrorMessageMapper.map(ConnectException()))
        assertEquals(ErrorMessage(R.string.error_network), ErrorMessageMapper.map(IOException()))
    }

    @Test
    fun `maps invalid json`() {
        assertEquals(ErrorMessage(R.string.error_invalid_data), ErrorMessageMapper.map(JsonSyntaxException("bad json")))
    }

    @Test
    fun `falls back to generic message`() {
        assertEquals(ErrorMessage(R.string.error_generic_message), ErrorMessageMapper.map(IllegalStateException()))
    }
}
