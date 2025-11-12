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

class SelectBarberActivity : AppCompatActivity() {

    private lateinit var recyclerViewBarbers: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var apiService: ApiService
    private lateinit var barberAdapter: BarberAdapter

    // Constante para identificar a requisição de seleção de barbeiro
    companion object {
        const val REQUEST_CODE_SELECT_BARBER = 2
        const val EXTRA_UNIT_ID = "EXTRA_UNIT_ID"
    }

    private var selectedUnitId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_barber)

        recyclerViewBarbers = findViewById(R.id.recyclerViewBarbers)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // 1. Receber o unitId da Intent
        selectedUnitId = intent.getStringExtra(EXTRA_UNIT_ID)

        // 2. Configurar Retrofit e ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/") // Usar o mesmo IP local do XAMPP
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        barberAdapter = BarberAdapter(mutableListOf()) { selectedBarber ->
            // Quando um barbeiro é clicado, retorna o ID e nome para a AgendamentoActivity
            val resultIntent = Intent().apply {
                putExtra("SELECTED_BARBER_ID", selectedBarber.id)
                putExtra("SELECTED_BARBER_NAME", selectedBarber.name)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerViewBarbers.adapter = barberAdapter

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
                    Toast.makeText(this, "Você já está na tela de Seleção de Barbeiro", Toast.LENGTH_SHORT).show()
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
        fetchBarbers()
    }

    private fun fetchBarbers() {
        apiService.getBarbers().enqueue(object : Callback<List<Barbeiro>> {
            override fun onResponse(call: Call<List<Barbeiro>>, response: Response<List<Barbeiro>>) {
                if (response.isSuccessful) {
                    val allBarbers = response.body() ?: emptyList()
                    // Aplicar a lógica de filtragem após obter todos os barbeiros da API
                    val filteredBarbers = if (selectedUnitId != null) {
                        allBarbers.filter { it.unitId == selectedUnitId }
                    } else {
                        allBarbers // Se nenhum unitId for fornecido, mostra todos
                    }
                    barberAdapter.updateDataSet(filteredBarbers)
                } else {
                    Log.e("API Error", "Falha ao carregar os barbeiros. Código: ${response.code()}")
                    Toast.makeText(this@SelectBarberActivity, "Erro ao carregar os barbeiros.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Barbeiro>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar os barbeiros", t)
                Toast.makeText(this@SelectBarberActivity, "Erro de conexão ao carregar os barbeiros.", Toast.LENGTH_LONG).show()
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