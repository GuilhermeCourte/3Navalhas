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
    private lateinit var adapter: CustomAdapter
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
                    // Adicionar flags para limpar o back stack e tratar WelcomeActivity como a nova raiz da navegação.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish() // Finaliza MainActivity para que WelcomeActivity seja a nova tela inicial do app
                    true
                }
                R.id.navigation_services -> {
                    Toast.makeText(this, "Você já está na tela de Serviços", Toast.LENGTH_SHORT).show()
                    true // Já estamos na tela de serviços/produtos
                }
                R.id.navigation_schedule -> {
                    Toast.makeText(this, "Agendar clicado", Toast.LENGTH_SHORT).show()
                    // Implementar navegação para a tela de agendamento
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
            .baseUrl("http://192.168.15.9/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    override fun onResume() {
        super.onResume()
        fetchProducts()
    }

    private fun fetchProducts() {
        apiService.getProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    adapter = CustomAdapter(produtos.toMutableList())
                    recyclerView.adapter = adapter
                } else {
                    Log.e("API Error", "Falha ao carregar os produtos. Código: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar os produtos", t)
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