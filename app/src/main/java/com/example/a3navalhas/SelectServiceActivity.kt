package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SelectServiceActivity : AppCompatActivity() {

    private lateinit var recyclerViewServices: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var apiService: ApiService
    private lateinit var serviceAdapter: ServiceAdapter

    // Constante para identificar a requisição de seleção de serviço
    companion object {
        const val REQUEST_CODE_SELECT_SERVICE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_service)

        recyclerViewServices = findViewById(R.id.recyclerViewServices)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // 1. Configurar Retrofit e ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/") // Usar o mesmo IP local do XAMPP
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        serviceAdapter = ServiceAdapter(mutableListOf()) { selectedService ->
            // Quando um serviço é clicado, retorna o ID e nome para a AgendamentoActivity
            val resultIntent = Intent().apply {
                putExtra("SELECTED_SERVICE_ID", selectedService.id)
                putExtra("SELECTED_SERVICE_NAME", selectedService.name)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerViewServices.adapter = serviceAdapter

        // Configurar Bottom Navigation View
        bottomNavigationView.selectedItemId = R.id.navigation_schedule

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_services -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_schedule -> {
                    Toast.makeText(this, "Você já está na tela de Seleção de Serviço", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_user -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchServices()
    }

    private fun fetchServices() {
        apiService.getServices().enqueue(object : Callback<List<Servico>> {
            override fun onResponse(call: Call<List<Servico>>, response: Response<List<Servico>>) {
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    serviceAdapter.updateDataSet(services)
                } else {
                    Log.e("API Error", "Falha ao carregar os serviços. Código: ${response.code()}")
                    Toast.makeText(this@SelectServiceActivity, "Erro ao carregar os serviços.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Servico>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar os serviços", t)
                Toast.makeText(this@SelectServiceActivity, "Erro de conexão ao carregar os serviços.", Toast.LENGTH_LONG).show()
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