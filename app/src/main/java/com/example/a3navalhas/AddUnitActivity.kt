package com.example.a3navalhas

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AddUnitActivity : AppCompatActivity() {

    private lateinit var unitNameEditText: EditText
    private lateinit var unitCityStateEditText: EditText
    private lateinit var unitAddressCepEditText: EditText
    private lateinit var unitImageUrlEditText: EditText
    private lateinit var saveUnitButton: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_unit)

        // Inicializa os elementos da UI
        unitNameEditText = findViewById(R.id.unitNameEditText)
        unitCityStateEditText = findViewById(R.id.unitCityStateEditText)
        unitAddressCepEditText = findViewById(R.id.unitAddressCepEditText)
        unitImageUrlEditText = findViewById(R.id.unitImageUrlEditText)
        saveUnitButton = findViewById(R.id.saveUnitButton)

        // Configura Retrofit e ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        saveUnitButton.setOnClickListener {
            val name = unitNameEditText.text.toString().trim()
            val cityState = unitCityStateEditText.text.toString().trim()
            val addressCep = unitAddressCepEditText.text.toString().trim()
            val imageUrl = unitImageUrlEditText.text.toString().trim()

            // Validação básica dos campos
            if (name.isEmpty() || cityState.isEmpty() || addressCep.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chamada da API para adicionar a unidade
            apiService.addUnit(
                name = name,
                cityState = cityState,
                addressCep = addressCep,
                imageUrl = if (imageUrl.isEmpty()) null else imageUrl // Envia null se a URL estiver vazia
            ).enqueue(object : Callback<ApiService.GenericResponse> {
                override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@AddUnitActivity, "Unidade adicionada com sucesso!", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val errorMessage = response.body()?.message ?: "Erro desconhecido ao adicionar unidade."
                        Toast.makeText(this@AddUnitActivity, "Erro ao adicionar unidade: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.e("API Error", "Falha ao adicionar unidade. Código: ${response.code()}, Mensagem: ${response.body()?.message}")
                    }
                }

                override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                    Toast.makeText(this@AddUnitActivity, "Erro de conexão ao adicionar unidade.", Toast.LENGTH_LONG).show()
                    Log.e("API Failure", "Erro ao adicionar unidade", t)
                }
            })
        }
    }

    private fun configureOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}