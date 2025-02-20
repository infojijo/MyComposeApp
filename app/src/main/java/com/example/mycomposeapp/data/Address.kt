package com.example.mycomposeapp.data

data class Address(
    val street: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val country: String = "Canada",
    val fullAddress: String
) {
    fun toShortString(): String {
        return "$fullAddress, $city"
    }

    fun toFullString(): String {
        return buildString {
            append("$fullAddress ")
            append(street)
            if (city.isNotEmpty()) append(", $city")
            if (province.isNotEmpty()) append(", $province")
            if (postalCode.isNotEmpty()) append(" $postalCode")
        }
    }

    override fun toString(): String = toFullString()
}
