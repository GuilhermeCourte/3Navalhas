package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceDisplayAdapter: ServiceDisplayAdapter // Renomeado de CustomAdapter
    private lateinit var apiService: ApiService
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produtos)

        recyclerView = findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        // Garante que o item "Serviços" esteja selecionado ao entrar nesta Activity
        bottomNavigationView.selectedItemId = R.id.navigation_services

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
                    Toast.makeText(this, "Você já está na tela de Serviços", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_schedule -> {
                    val intent = Intent(this, AgendamentoActivity::class.java)
                    startActivity(intent)
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

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        serviceDisplayAdapter = ServiceDisplayAdapter(mutableListOf()) // Inicializa com lista vazia
        recyclerView.adapter = serviceDisplayAdapter

        // O FloatingActionButton foi removido do layout activity_produtos.xml,
        // então o código relacionado a ele também foi removido daqui.
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
                    // ADICIONANDO LOG PARA DEPURACAO
                    services.forEach { servico ->
                        Log.d("MainActivity", "Service: ${servico.name}, ImageUrl: ${servico.imageUrl}")
                    }
                    // FIM DO LOG DE DEPURACAO
                    serviceDisplayAdapter.updateDataSet(services) // Atualiza o adapter com os serviços da API
                } else {
                    Log.e("API Error", "Falha ao carregar os serviços. Código: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Erro ao carregar os serviços.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Servico>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar os serviços", t)
                Toast.makeText(this@MainActivity, "Erro de conexão ao carregar os serviços.", Toast.LENGTH_LONG).show()
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