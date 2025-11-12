package com.example.a3navalhas

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Barbeiro(
    val id: String,
    val name: String,
    val specialization: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("unit_id") val unitId: String,
    @SerializedName("unit_name") val unitName: String? // Novo campo para o nome da unidade
) : Serializable // Torna a classe "passável" entre activities
