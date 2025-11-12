package com.example.a3navalhas

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class AddServiceActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var imageUrlEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service) // Usando o novo layout

        nameEditText = findViewById(R.id.serviceNameEditText)
        descriptionEditText = findViewById(R.id.serviceDescriptionEditText)
        priceEditText = findViewById(R.id.servicePriceEditText)
        imageUrlEditText = findViewById(R.id.serviceImageUrlEditText)
        durationEditText = findViewById(R.id.serviceDurationEditText)
        saveButton = findViewById(R.id.saveServiceButton)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        saveButton.setOnClickListener {
            val newService = Servico(
                id = "", // O ID será gerado pelo backend
                name = nameEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0, // Converte para Double
                duration = durationEditText.text.toString().toIntOrNull() ?: 0, // Converte para Int
                imageUrl = imageUrlEditText.text.toString()
            )

            apiService.addService(
                name = newService.name,
                description = newService.description,
                price = newService.price,
                duration = newService.duration,
                imageUrl = newService.imageUrl
            ).enqueue(object : Callback<ApiService.GenericResponse> {
                override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@AddServiceActivity, "Serviço adicionado com sucesso!", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val errorMessage = response.body()?.message ?: "Erro desconhecido ao adicionar serviço."
                        Toast.makeText(this@AddServiceActivity, "Erro na inclusão: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.e("API Error", "Falha ao adicionar serviço. Código: ${response.code()}, Mensagem: ${response.body()?.message}")
                    }
                }

                override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                    Toast.makeText(this@AddServiceActivity, "Erro ao adicionar o serviço", Toast.LENGTH_LONG).show()
                    Log.e("API Failure", "Erro ao adicionar o serviço", t)
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