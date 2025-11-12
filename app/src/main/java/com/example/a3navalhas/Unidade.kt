package com.example.a3navalhas

import com.google.gson.annotations.SerializedName

data class Unidade(
    val id: String,
    val name: String,
    @SerializedName("city_state") val cityState: String,
    @SerializedName("address_cep") val addressCep: String,
    @SerializedName("image_url") val imageUrl: String?
)