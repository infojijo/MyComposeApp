package com.example.mycomposeapp.api

import com.example.mycomposeapp.data.Address

class CanadaPostRepository(private val apiKey: String) {
    private val api = CanadaPostApi(apiKey)

    suspend fun searchAddress(query: String): List<Address> {
        return try {
            val response = api.findAddress(query = query)
            response.items.map { it.toAddress() }
        } catch (e: Exception) {
            // Log the error in a production app
            emptyList()
        }
    }
}
