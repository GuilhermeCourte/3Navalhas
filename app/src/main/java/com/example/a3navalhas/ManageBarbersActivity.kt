package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

class ManageBarbersActivity : AppCompatActivity() {

    private lateinit var recyclerViewManageBarbers: RecyclerView
    private lateinit var fabAddBarber: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var apiService: ApiService
    private lateinit var manageBarberAdapter: ManageBarberAdapter

    companion object {
        const val REQUEST_CODE_ADD_BARBER = 103
        const val REQUEST_CODE_EDIT_BARBER = 104 // Novo código para edição
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_barbers)

        recyclerViewManageBarbers = findViewById(R.id.recyclerViewManageBarbers)
        fabAddBarber = findViewById(R.id.fabAddBarber)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        manageBarberAdapter = ManageBarberAdapter(mutableListOf(),
            onEditClick = { barber ->
                // Abre a tela de edição, passando os dados do barbeiro
                val intent = Intent(this, EditBarberActivity::class.java)
                intent.putExtra("EXTRA_BARBER", barber) // Passa o objeto inteiro
                startActivityForResult(intent, REQUEST_CODE_EDIT_BARBER)
            },
            onDeleteClick = { barber, position ->
                AlertDialog.Builder(this)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Tem certeza que deseja excluir o barbeiro '${barber.name}'?")
                    .setPositiveButton("Sim") { _, _ ->
                        deleteBarber(barber.id, position)
                    }
                    .setNegativeButton("Não", null)
                    .show()
            }
        )
        recyclerViewManageBarbers.adapter = manageBarberAdapter

        fabAddBarber.setOnClickListener {
            val intent = Intent(this, AddBarberActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_BARBER)
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
                    Toast.makeText(this, "Você já está na tela de Gerenciar Barbeiros", Toast.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Atualiza a lista se um barbeiro foi adicionado ou editado com sucesso
        if ((requestCode == REQUEST_CODE_ADD_BARBER || requestCode == REQUEST_CODE_EDIT_BARBER) && resultCode == RESULT_OK) {
            fetchBarbers()
        }
    }

    private fun fetchBarbers() {
        apiService.getBarbers().enqueue(object : Callback<List<Barbeiro>> {
            override fun onResponse(call: Call<List<Barbeiro>>, response: Response<List<Barbeiro>>) {
                if (response.isSuccessful) {
                    manageBarberAdapter.updateDataSet(response.body() ?: emptyList())
                } else {
                    Log.e("API Error", "Falha ao carregar os barbeiros. Código: ${response.code()}")
                    Toast.makeText(this@ManageBarbersActivity, "Erro ao carregar os barbeiros.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Barbeiro>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar os barbeiros", t)
                Toast.makeText(this@ManageBarbersActivity, "Erro de conexão ao carregar os barbeiros.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteBarber(barberId: String, position: Int) {
        apiService.deleteBarber(barberId).enqueue(object : Callback<ApiService.GenericResponse> {
            override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@ManageBarbersActivity, "Barbeiro excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    manageBarberAdapter.removeItem(position)
                } else {
                    val errorMessage = response.body()?.message ?: "Erro desconhecido"
                    Toast.makeText(this@ManageBarbersActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API Error", "Falha ao excluir. Código: ${response.code()}, Msg: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                Toast.makeText(this@ManageBarbersActivity, "Erro de conexão ao excluir.", Toast.LENGTH_LONG).show()
                Log.e("API Failure", "Erro ao excluir", t)
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