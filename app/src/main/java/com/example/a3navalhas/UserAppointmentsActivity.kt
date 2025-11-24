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

class UserAppointmentsActivity : AppCompatActivity() {

    private lateinit var recyclerViewUserAppointments: RecyclerView
    private lateinit var agendamentoAdapter: AgendamentoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewMyAppointmentsTitle: TextView

    private val api: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            .baseUrl("http://192.168.15.53/3navalhas_api/") // Verifique e ajuste o IP
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

        agendamentoAdapter = AgendamentoAdapter(mutableListOf<Agendamento>())
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appointments = api.getAgendamentosPorCliente(userPhone)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (appointments.isNotEmpty()) {
                        agendamentoAdapter.updateDataSet(appointments)
                    } else {
                        Toast.makeText(this@UserAppointmentsActivity, "Nenhum agendamento encontrado para este telefone.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Log.e("UserAppointmentsActivity", "Erro ao carregar agendamentos do usuário: ", e)
                    Toast.makeText(this@UserAppointmentsActivity, "Falha ao carregar agendamentos.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
