package com.example.a3navalhas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Field

class IncluirProdutoActivity : AppCompatActivity() {
    private lateinit var nomeEditText: EditText
    private lateinit var descricaoEditText: EditText
    private lateinit var precoEditText: EditText
    private lateinit var imagemEditText: EditText
    private lateinit var salvarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incluir_produto)

        nomeEditText = findViewById(R.id.nomeEditText)
        descricaoEditText = findViewById(R.id.descricaoEditText)
        precoEditText = findViewById(R.id.precoEditText)
        imagemEditText = findViewById(R.id.imagemEditText)
        salvarButton = findViewById(R.id.salvarButton)

        // Configuração do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.135.109.37/3navalhas_api/") // Substitua pelo seu endereço base
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        salvarButton.setOnClickListener {
            // Fazer a requisição para incluir o produto
            apiService.incluirProduto(
                nomeEditText.text.toString(),
                descricaoEditText.text.toString(),
                precoEditText.text.toString(),
                imagemEditText.text.toString()
            ).enqueue(object : Callback<IncluirProdutoResponse> {
                override fun onResponse(call: Call<IncluirProdutoResponse>, response: Response<IncluirProdutoResponse>) {
                    // A resposta é bem-sucedida se o status HTTP for 200 e a resposta não for nula.
                    if (response.isSuccessful && response.body() != null) {
                        // Aqui a tela é finalizada, voltando para a tela principal
                        Toast.makeText(this@IncluirProdutoActivity, response.body()!!.status, Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@IncluirProdutoActivity, "Erro na inclusão", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<IncluirProdutoResponse>, t: Throwable) {
                    Toast.makeText(this@IncluirProdutoActivity, "Erro ao incluir o produto", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}