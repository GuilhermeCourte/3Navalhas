package com.example.a3navalhas

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("produtos.php")
    fun getProdutos(): Call<List<Produto>>

    @FormUrlEncoded
    @POST("incluir_produto.php")
    fun incluirProduto(
        @Field("PRODUTO_NOME") nome: String,
        @Field("PRODUTO_DESC") descricao: String,
        @Field("PRODUTO_PRECO") preco: String,
        @Field("PRODUTO_IMAGEM") imagem: String
    ): Call<IncluirProdutoResponse>

    @FormUrlEncoded
    @POST("editar_produto.php")
    fun editarProduto(
        @Field("PRODUTO_ID") id: Int,
        @Field("PRODUTO_NOME") nome: String,
        @Field("PRODUTO_DESC") descricao: String,
        @Field("PRODUTO_PRECO") preco: String,
        @Field("PRODUTO_IMAGEM") imagem: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("deletar_produto.php")
    fun deletarProduto(
        @Field("PRODUTO_ID") id: Int
    ): Call<Void>
}
