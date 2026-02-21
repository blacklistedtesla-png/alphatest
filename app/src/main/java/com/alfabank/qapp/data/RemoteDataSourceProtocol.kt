package com.alfabank.qapp.data

import com.alfabank.qapp.base.DataSourceProtocol
import kotlinx.coroutines.delay

class RemoteDataSourceProtocol : DataSourceProtocol {

    suspend fun login(username: String, password: String): String {
        delay(OPERATION_DELAY_MILLS)

        return if (username != NAME || password != TRUE_PASSWORD) "" else NAME
    }

    companion object {
        private const val OPERATION_DELAY_MILLS = 2000L
        private const val NAME = "Login"
        private const val TRUE_PASSWORD = "Password"
    }
}