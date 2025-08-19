package com.example.mycomposeapp.api

import com.example.mycomposeapp.data.Address
import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.serialization.gson.gson

data class AddressResponse(
    @SerializedName("Items")
    val items: List<AddressItem>
)

data class AddressItem(
    @SerializedName("Id")
    val id: String,
    @SerializedName("Text")
    val text: String,
    @SerializedName("Description")
    val description: String
) {
    fun toAddress(): Address {
        // Parse the description which contains the full address
        val parts = description.split(",").map { it.trim() }
        return when (parts.size) {
            3 -> Address(
                fullAddress = text,
                street = parts[0],
                city = parts[1],
                province = parts[2].substringBefore(" "),
                postalCode = parts[2].substringAfter(" ").trim()
            )
            else -> Address(
                fullAddress = text,
                street = text,
                city = "",
                province = "",
                postalCode = ""
            )
        }
    }
}

class CanadaPostApi(private val apiKey: String) {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    private val baseUrl = "https://ws1.postescanada-canadapost.ca/AddressComplete/Interactive/Find/v2.10/json3.ws"

    suspend fun findAddress(
        query: String,
        country: String = "CAN",
        language: String = "en",
        lastId: String = "",
        searchFor: String = "Everything"
    ): AddressResponse {
        return client.get(baseUrl) {
            url {
                parameters.append("Key", apiKey)
                parameters.append("SearchTerm", query)
                parameters.append("Country", country)
                parameters.append("LanguagePreference", language)
                parameters.append("LastId", lastId)
                parameters.append("SearchFor", searchFor)
            }
        }.body()
    }
}
