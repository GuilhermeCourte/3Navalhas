package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
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
import android.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.a3navalhas.Agendamento 
import com.example.a3navalhas.Constants 

class AdminActivity : AppCompatActivity() {

    private lateinit var buttonManageServices: MaterialButton
    private lateinit var buttonManageUnits: MaterialButton
    private lateinit var buttonManageBarbers: MaterialButton
    private lateinit var recyclerViewAppointments: RecyclerView
    private lateinit var agendamentoAdapter: AgendamentoAdapter
    private lateinit var apiService: ApiService
    private lateinit var textViewNoAppointments: TextView // NOVO: TextView para agendamentos vazios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        buttonManageServices = findViewById(R.id.buttonManageServices)
        buttonManageUnits = findViewById(R.id.buttonManageUnits)
        buttonManageBarbers = findViewById(R.id.buttonManageBarbers)
        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments)
        textViewNoAppointments = findViewById(R.id.textViewNoAppointments) // NOVO: Inicializar o TextView

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // CORRIGIDO: Inicialização correta do adapter com todos os parâmetros
        agendamentoAdapter = AgendamentoAdapter(
            dataSet = mutableListOf(),
            onDeleteClick = { agendamento, position ->
                showDeleteConfirmationDialog(agendamento, position)
            },
            showDeleteButton = true
        )

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
        // Antes de carregar, assuma que não há agendamentos
        textViewNoAppointments.visibility = View.GONE
        recyclerViewAppointments.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appointments = apiService.getAgendamentos()
                withContext(Dispatchers.Main) {
                    if (appointments.isNotEmpty()) {
                        agendamentoAdapter.updateDataSet(appointments)
                        recyclerViewAppointments.visibility = View.VISIBLE
                        textViewNoAppointments.visibility = View.GONE
                    } else {
                        agendamentoAdapter.updateDataSet(emptyList())
                        recyclerViewAppointments.visibility = View.GONE
                        textViewNoAppointments.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("AdminActivity", "Erro ao carregar agendamentos: ", e)
                    Toast.makeText(this@AdminActivity, "Erro ao carregar agendamentos.", Toast.LENGTH_LONG).show()
                    recyclerViewAppointments.visibility = View.GONE
                    textViewNoAppointments.visibility = View.VISIBLE // Exibir mensagem de erro/vazio em caso de falha
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(agendamento: Agendamento, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("""Tem certeza que deseja excluir o agendamento de '${agendamento.nome_cliente}' em '${agendamento.data_agendamento} às ${agendamento.hora_agendamento}'?""")
            .setPositiveButton("Sim") { dialog, which ->
                deleteAppointment(agendamento.id, position)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deleteAppointment(appointmentId: String, position: Int) {
        apiService.deleteAgendamento(appointmentId).enqueue(object : Callback<ApiService.GenericResponse> {
            override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminActivity, "Agendamento excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    agendamentoAdapter.removeItem(position)
                    // Após remover, verificar se a lista ficou vazia para mostrar/ocultar o TextView
                    if (agendamentoAdapter.itemCount == 0) {
                        recyclerViewAppointments.visibility = View.GONE
                        textViewNoAppointments.visibility = View.VISIBLE
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Erro desconhecido ao excluir agendamento."
                    Toast.makeText(this@AdminActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API Error", "Falha ao excluir agendamento. Código: ${response.code()}, Mensagem: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                Toast.makeText(this@AdminActivity, "Erro de conexão ao excluir agendamento.", Toast.LENGTH_LONG).show()
                Log.e("API Failure", "Erro ao excluir agendamento", t)
            }
        })
    }
}