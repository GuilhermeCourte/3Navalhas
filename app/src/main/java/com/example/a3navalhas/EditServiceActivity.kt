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

class EditServiceActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var imageUrlEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var saveButton: Button
    private var serviceId: String? = null
    private lateinit var apiService: ApiService

    companion object {
        const val EXTRA_SERVICE_ID = "EXTRA_SERVICE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service) // Usando o novo layout

        nameEditText = findViewById(R.id.serviceNameEditText)
        descriptionEditText = findViewById(R.id.serviceDescriptionEditText)
        priceEditText = findViewById(R.id.servicePriceEditText)
        imageUrlEditText = findViewById(R.id.serviceImageUrlEditText)
        durationEditText = findViewById(R.id.serviceDurationEditText)
        saveButton = findViewById(R.id.saveServiceButton)

        serviceId = intent.getStringExtra(EXTRA_SERVICE_ID)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        serviceId?.let { id ->
            fetchServiceDetails(id)
        } ?: run {
            Toast.makeText(this, "ID do serviço não fornecido.", Toast.LENGTH_SHORT).show()
            finish()
        }

        saveButton.setOnClickListener {
            if (serviceId == null) {
                Toast.makeText(this, "Não foi possível atualizar: ID do serviço ausente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedService = Servico(
                id = serviceId!!,
                name = nameEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0,
                duration = durationEditText.text.toString().toIntOrNull() ?: 0,
                imageUrl = imageUrlEditText.text.toString()
            )

            // ADICIONANDO ESTES LOGS PARA DEPURAR
            Log.d("EditServiceActivity", "Attempting to update service with ID: ${updatedService.id}")
            Log.d("EditServiceActivity", "Name: ${updatedService.name}, Price: ${updatedService.price}, Duration: ${updatedService.duration}, ImageUrl: ${updatedService.imageUrl}")
            // FIM DOS LOGS DE DEPURACAO

            apiService.updateService(
                id = updatedService.id,
                name = updatedService.name,
                description = updatedService.description,
                price = updatedService.price,
                duration = updatedService.duration,
                imageUrl = updatedService.imageUrl
            ).enqueue(object : Callback<ApiService.GenericResponse> {
                override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@EditServiceActivity, "Serviço atualizado com sucesso!", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val errorMessage = response.body()?.message ?: "Erro desconhecido na atualização."
                        Toast.makeText(this@EditServiceActivity, "Erro na atualização: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.e("API Error", "Falha ao atualizar serviço. Código: ${response.code()}, Mensagem: ${response.body()?.message}")
                    }
                }

                override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                    Toast.makeText(this@EditServiceActivity, "Erro ao atualizar o serviço", Toast.LENGTH_LONG).show()
                    Log.e("API Failure", "Erro ao atualizar o serviço", t)
                }
            })
        }
    }

    private fun fetchServiceDetails(id: String) {
        apiService.getServiceById(id).enqueue(object : Callback<Servico> {
            override fun onResponse(call: Call<Servico>, response: Response<Servico>) {
                if (response.isSuccessful) {
                    val service = response.body()
                    service?.let {
                        nameEditText.setText(it.name)
                        descriptionEditText.setText(it.description)
                        priceEditText.setText(it.price.toString())
                        imageUrlEditText.setText(it.imageUrl)
                        durationEditText.setText(it.duration.toString())
                    } ?: run {
                        Toast.makeText(this@EditServiceActivity, "Serviço não encontrado.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@EditServiceActivity, "Erro ao carregar detalhes do serviço.", Toast.LENGTH_LONG).show()
                    Log.e("API Error", "Falha ao carregar detalhes do serviço. Código: ${response.code()}")
                    finish()
                }
            }

            override fun onFailure(call: Call<Servico>, t: Throwable) {
                Toast.makeText(this@EditServiceActivity, "Erro de conexão ao carregar detalhes do serviço.", Toast.LENGTH_LONG).show()
                Log.e("API Failure", "Erro ao carregar detalhes do serviço", t)
                finish()
            }
        })
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