package com.example.a3navalhas

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // --- Endpoints para Serviços ---
    @GET("get_services.php")
    fun getServices(): Call<List<Servico>>

    @GET("get_service.php")
    fun getServiceById(@Query("id") id: String): Call<Servico>

    @FormUrlEncoded
    @POST("add_service.php")
    fun addService(
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("price") price: Double,
        @Field("duration") duration: Int,
        @Field("image_url") imageUrl: String? // Pode ser nulo
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("update_service.php")
    fun updateService(
        @Field("id") id: String,
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("price") price: Double,
        @Field("duration") duration: Int,
        @Field("image_url") imageUrl: String?
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("delete_service.php")
    fun deleteService(@Field("id") id: String): Call<GenericResponse>


    // --- Endpoints para Unidades ---
    @GET("get_units.php")
    fun getUnits(): Call<List<Unidade>>

    @GET("get_unit.php")
    fun getUnitById(@Query("id") id: String): Call<Unidade>

    @FormUrlEncoded
    @POST("add_unit.php")
    fun addUnit(
        @Field("name") name: String,
        @Field("city_state") cityState: String,
        @Field("address_cep") addressCep: String,
        @Field("image_url") imageUrl: String?
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("update_unit.php")
    fun updateUnit(
        @Field("id") id: String,
        @Field("name") name: String,
        @Field("city_state") cityState: String,
        @Field("address_cep") addressCep: String,
        @Field("image_url") imageUrl: String?
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("delete_unit.php")
    fun deleteUnit(@Field("id") id: String): Call<GenericResponse>


    // --- Endpoints para Barbeiros ---
    @GET("get_barbers.php")
    fun getBarbers(): Call<List<Barbeiro>>

    @GET("get_barber.php")
    fun getBarberById(@Query("id") id: String): Call<Barbeiro>

    @FormUrlEncoded
    @POST("add_barber.php")
    fun addBarber(
        @Field("name") name: String,
        @Field("specialization") specialization: String,
        @Field("image_url") imageUrl: String?,
        @Field("unit_id") unitId: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("update_barber.php")
    fun updateBarber(
        @Field("id") id: String,
        @Field("name") name: String,
        @Field("specialization") specialization: String,
        @Field("image_url") imageUrl: String?,
        @Field("unit_id") unitId: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("delete_barber.php")
    fun deleteBarber(@Field("id") id: String): Call<GenericResponse>

    // --- Data Classes de Resposta --- (assumindo que Servico, Unidade, Barbeiro e LoginResponse já estão definidas e acessíveis)
    data class GenericResponse(val status: String, val message: String)
}
