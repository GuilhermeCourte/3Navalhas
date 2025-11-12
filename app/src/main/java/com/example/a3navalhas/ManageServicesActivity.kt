package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.app.AlertDialog

class ManageServicesActivity : AppCompatActivity() {

    private lateinit var recyclerViewManageServices: RecyclerView
    private lateinit var fabAddService: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var apiService: ApiService
    private lateinit var manageServiceAdapter: ManageServiceAdapter

    // Launcher para adicionar/editar serviço e receber resultado
    private val serviceActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            fetchServices() // Recarregar a lista após adicionar/editar com sucesso
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_services)

        recyclerViewManageServices = findViewById(R.id.recyclerViewManageServices)
        fabAddService = findViewById(R.id.fabAddService)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        manageServiceAdapter = ManageServiceAdapter(mutableListOf(),
            onEditClick = { service ->
                val intent = Intent(this, EditServiceActivity::class.java).apply {
                    putExtra(EditServiceActivity.EXTRA_SERVICE_ID, service.id)
                }
                serviceActivityResultLauncher.launch(intent)
            },
            onDeleteClick = { service, position ->
                AlertDialog.Builder(this)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Tem certeza que deseja excluir o serviço '${service.name}'?")
                    .setPositiveButton("Sim") { dialog, which ->
                        deleteService(service.id, position)
                    }
                    .setNegativeButton("Não", null)
                    .show()
            }
        )
        recyclerViewManageServices.adapter = manageServiceAdapter

        fabAddService.setOnClickListener {
            val intent = Intent(this, AddServiceActivity::class.java)
            serviceActivityResultLauncher.launch(intent)
        }

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
                    Toast.makeText(this, "Você já está na tela de Gerenciar Serviços", Toast.LENGTH_SHORT).show()
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
        fetchServices() // Chamando fetchServices() aqui para garantir que a lista seja sempre carregada/atualizada
    }

    private fun fetchServices() {
        apiService.getServices().enqueue(object : Callback<List<Servico>> {
            override fun onResponse(call: Call<List<Servico>>, response: Response<List<Servico>>) {
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    manageServiceAdapter.updateDataSet(services)
                } else {
                    Log.e("API Error", "Falha ao carregar os serviços. Código: ${response.code()}")
                    Toast.makeText(this@ManageServicesActivity, "Erro ao carregar os serviços.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Servico>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar os serviços", t)
                Toast.makeText(this@ManageServicesActivity, "Erro de conexão ao carregar os serviços.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteService(serviceId: String, position: Int) {
        apiService.deleteService(serviceId).enqueue(object : Callback<ApiService.GenericResponse> {
            override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@ManageServicesActivity, "Serviço excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    manageServiceAdapter.removeItem(position)
                } else {
                    val errorMessage = response.body()?.message ?: "Erro desconhecido ao excluir serviço."
                    Toast.makeText(this@ManageServicesActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API Error", "Falha ao excluir serviço. Código: ${response.code()}, Mensagem: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                Toast.makeText(this@ManageServicesActivity, "Erro de conexão ao excluir serviço.", Toast.LENGTH_LONG).show()
                Log.e("API Failure", "Erro ao excluir serviço", t)
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