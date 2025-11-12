package com.example.a3navalhas

import com.google.gson.annotations.SerializedName

data class Servico(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val duration: Int,
    @SerializedName("image_url") val imageUrl: String? // Mapeia 'image_url' do JSON
)