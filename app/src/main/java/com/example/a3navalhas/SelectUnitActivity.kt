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

class SelectUnitActivity : AppCompatActivity() {

    private lateinit var recyclerViewUnits: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var apiService: ApiService
    private lateinit var unitAdapter: UnitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_unit)

        recyclerViewUnits = findViewById(R.id.recyclerViewUnits)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // 1. Configurar Retrofit e ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/") // Usar o mesmo IP local do XAMPP
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        unitAdapter = UnitAdapter(mutableListOf()) { selectedUnit ->
            // Quando uma unidade é clicada, retorna o ID para a AgendamentoActivity
            val resultIntent = Intent().apply {
                putExtra("SELECTED_UNIT_ID", selectedUnit.id)
                putExtra("SELECTED_UNIT_NAME", selectedUnit.name)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerViewUnits.adapter = unitAdapter

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
                    Toast.makeText(this, "Você já está na tela de Seleção de Unidade", Toast.LENGTH_SHORT).show()
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
        fetchUnits()
    }

    private fun fetchUnits() {
        apiService.getUnits().enqueue(object : Callback<List<Unidade>> {
            override fun onResponse(call: Call<List<Unidade>>, response: Response<List<Unidade>>) {
                if (response.isSuccessful) {
                    val units = response.body() ?: emptyList()
                    unitAdapter.updateDataSet(units) // Novo método para atualizar o Adapter
                } else {
                    Log.e("API Error", "Falha ao carregar as unidades. Código: ${response.code()}")
                    Toast.makeText(this@SelectUnitActivity, "Erro ao carregar as unidades.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Unidade>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar as unidades", t)
                Toast.makeText(this@SelectUnitActivity, "Erro de conexão ao carregar as unidades.", Toast.LENGTH_LONG).show()
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