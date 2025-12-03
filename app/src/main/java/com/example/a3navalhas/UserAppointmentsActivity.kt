package com.example.a3navalhas

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.a3navalhas.Constants 
import android.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserAppointmentsActivity : AppCompatActivity() {


    private lateinit var recyclerViewUserAppointments: RecyclerView
    private lateinit var agendamentoAdapter: AgendamentoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewMyAppointmentsTitle: TextView
    private lateinit var textViewNoUserAppointments: TextView // NOVO: TextView para agendamentos vazios

    private val api: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_appointments)

        recyclerViewUserAppointments = findViewById(R.id.recyclerViewUserAppointments)
        progressBar = findViewById(R.id.progressBarUserAppointments)
        textViewMyAppointmentsTitle = findViewById(R.id.textViewMyAppointmentsTitle)
        textViewNoUserAppointments = findViewById(R.id.textViewNoUserAppointments) // NOVO: Inicializar o TextView

        agendamentoAdapter = AgendamentoAdapter(
            dataSet = mutableListOf(),
            onDeleteClick = { agendamento, position ->
                showDeleteConfirmationDialog(agendamento, position)
            },
            showDeleteButton = true 
        )

        recyclerViewUserAppointments.layoutManager = LinearLayoutManager(this)
        recyclerViewUserAppointments.adapter = agendamentoAdapter

        val userPhone = intent.getStringExtra(AgendamentoActivity.EXTRA_USER_PHONE)

        if (userPhone != null) {
            fetchUserAppointments(userPhone)
        } else {
            Toast.makeText(this, "Telefone do usuário não encontrado.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun fetchUserAppointments(userPhone: String) {
        progressBar.visibility = View.VISIBLE
        // Antes de carregar, assuma que não há agendamentos
        textViewNoUserAppointments.visibility = View.GONE
        recyclerViewUserAppointments.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appointments = api.getAgendamentosPorCliente(userPhone)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (appointments.isNotEmpty()) {
                        agendamentoAdapter.updateDataSet(appointments)
                        recyclerViewUserAppointments.visibility = View.VISIBLE
                        textViewNoUserAppointments.visibility = View.GONE
                    } else {
                        agendamentoAdapter.updateDataSet(emptyList())
                        recyclerViewUserAppointments.visibility = View.GONE
                        textViewNoUserAppointments.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Log.e("UserAppointmentsActivity", "Erro ao carregar agendamentos do usuário: ", e)
                    Toast.makeText(this@UserAppointmentsActivity, "Falha ao carregar agendamentos.", Toast.LENGTH_LONG).show()
                    recyclerViewUserAppointments.visibility = View.GONE
                    textViewNoUserAppointments.visibility = View.VISIBLE // Exibir mensagem de erro/vazio em caso de falha
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(agendamento: Agendamento, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("""Tem certeza que deseja excluir o seu agendamento de '${agendamento.nome_cliente}' em '${agendamento.data_agendamento} às ${agendamento.hora_agendamento}'?""")
            .setPositiveButton("Sim") { dialog, which ->
                deleteAppointment(agendamento.id, position)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deleteAppointment(appointmentId: String, position: Int) {
        api.deleteAgendamento(appointmentId).enqueue(object : Callback<ApiService.GenericResponse> {
            override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@UserAppointmentsActivity, "Agendamento excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    agendamentoAdapter.removeItem(position)
                    // Após remover, verificar se a lista ficou vazia para mostrar/ocultar o TextView
                    if (agendamentoAdapter.itemCount == 0) {
                        recyclerViewUserAppointments.visibility = View.GONE
                        textViewNoUserAppointments.visibility = View.VISIBLE
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Erro desconhecido ao excluir agendamento."
                    Toast.makeText(this@UserAppointmentsActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API Error", "Falha ao excluir agendamento. Código: ${response.code()}, Mensagem: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                Toast.makeText(this@UserAppointmentsActivity, "Erro de conexão ao excluir agendamento.", Toast.LENGTH_LONG).show()
                Log.e("API Failure", "Erro ao excluir agendamento", t)
            }
        })
    }
}