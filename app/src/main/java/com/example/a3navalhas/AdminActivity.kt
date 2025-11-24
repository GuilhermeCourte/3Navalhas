package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.a3navalhas.Agendamento // Adicionando a importação para a classe Agendamento

class AdminActivity : AppCompatActivity() {

    private lateinit var buttonManageServices: MaterialButton
    private lateinit var buttonManageUnits: MaterialButton
    private lateinit var buttonManageBarbers: MaterialButton
    private lateinit var recyclerViewAppointments: RecyclerView
    private lateinit var agendamentoAdapter: AgendamentoAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        buttonManageServices = findViewById(R.id.buttonManageServices)
        buttonManageUnits = findViewById(R.id.buttonManageUnits)
        buttonManageBarbers = findViewById(R.id.buttonManageBarbers)
        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments)

        // Configurar Retrofit para a API
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.15.53/3navalhas_api/") // Verifique e ajuste o IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Configurar RecyclerView para Agendamentos - CORREÇÃO AQUI
        agendamentoAdapter = AgendamentoAdapter(mutableListOf<Agendamento>())
        recyclerViewAppointments.layoutManager = LinearLayoutManager(this)
        recyclerViewAppointments.adapter = agendamentoAdapter

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        fetchAppointments()
    }

    private fun setupClickListeners() {
        buttonManageServices.setOnClickListener {
            val intent = Intent(this, ManageServicesActivity::class.java)
            startActivity(intent)
        }

        buttonManageUnits.setOnClickListener {
            val intent = Intent(this, ManageUnitsActivity::class.java)
            startActivity(intent)
        }

        buttonManageBarbers.setOnClickListener {
            val intent = Intent(this, ManageBarbersActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAppointments() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appointments = apiService.getAgendamentos()
                withContext(Dispatchers.Main) {
                    agendamentoAdapter.updateDataSet(appointments)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("AdminActivity", "Erro ao carregar agendamentos: ", e)
                    Toast.makeText(this@AdminActivity, "Erro ao carregar agendamentos.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
