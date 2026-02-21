package com.alfabank.qapp.domain

import com.alfabank.qapp.base.GatewayProtocol
import com.alfabank.qapp.data.RemoteDataSourceProtocol
import com.alfabank.qapp.data.model.LoginInputData

/**
 * Performs login request
 */
class LoginGateway(private val remoteDataSource: RemoteDataSourceProtocol) : GatewayProtocol {

    suspend fun login(loginInputData: LoginInputData): Boolean {
        return remoteDataSource.login(loginInputData.username, loginInputData.password).also {
            if (it.isNotEmpty()) {
                println("${this::class.simpleName}: logged as $it")
            } else {
                println("${this::class.simpleName}: login failed")
            }
        }.isNotEmpty()
    }
}