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

class ManageUnitsActivity : AppCompatActivity() {

    private lateinit var recyclerViewManageUnits: RecyclerView
    private lateinit var fabAddUnit: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var apiService: ApiService
    private lateinit var manageUnitAdapter: ManageUnitAdapter

    companion object {
        const val REQUEST_CODE_ADD_UNIT = 101
        const val REQUEST_CODE_EDIT_UNIT = 102 // Adicionando para futura implementação
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_units)

        recyclerViewManageUnits = findViewById(R.id.recyclerViewManageUnits)
        fabAddUnit = findViewById(R.id.fabAddUnit)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // 1. Configurar Retrofit e ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/") // Usar o mesmo IP local do XAMPP
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        manageUnitAdapter = ManageUnitAdapter(mutableListOf(),
            onEditClick = { unit ->
                Toast.makeText(this, "Editar Unidade: ${unit.name}", Toast.LENGTH_SHORT).show()
                // TODO: Implementar navegação para a tela de edição de unidade
            },
            onDeleteClick = { unit, position ->
                AlertDialog.Builder(this)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Tem certeza que deseja excluir a unidade '${unit.name}'?")
                    .setPositiveButton("Sim") { dialog, which ->
                        deleteUnit(unit.id, position)
                    }
                    .setNegativeButton("Não", null)
                    .show()
            }
        )
        recyclerViewManageUnits.adapter = manageUnitAdapter

        fabAddUnit.setOnClickListener {
            val intent = Intent(this, AddUnitActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_UNIT)
        }

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
                    Toast.makeText(this, "Você já está na tela de Gerenciar Unidades", Toast.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_UNIT && resultCode == RESULT_OK) {
            fetchUnits() // Atualiza a lista de unidades após adicionar uma nova
        }
    }

    private fun fetchUnits() {
        apiService.getUnits().enqueue(object : Callback<List<Unidade>> {
            override fun onResponse(call: Call<List<Unidade>>, response: Response<List<Unidade>>) {
                if (response.isSuccessful) {
                    val units = response.body() ?: emptyList()
                    // ADICIONANDO LOG PARA DEPURACAO
                    units.forEach { unidade ->
                        Log.d("ManageUnitsActivity", "Unit: ${unidade.name}, ImageUrl: ${unidade.imageUrl}")
                    }
                    // FIM DO LOG DE DEPURACAO
                    manageUnitAdapter.updateDataSet(units) // Alterado para usar o método updateDataSet
                } else {
                    Log.e("API Error", "Falha ao carregar as unidades. Código: ${response.code()}")
                    Toast.makeText(this@ManageUnitsActivity, "Erro ao carregar as unidades.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Unidade>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar as unidades", t)
                Toast.makeText(this@ManageUnitsActivity, "Erro de conexão ao carregar as unidades.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteUnit(unitId: String, position: Int) {
        apiService.deleteUnit(unitId).enqueue(object : Callback<ApiService.GenericResponse> {
            override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@ManageUnitsActivity, "Unidade excluída com sucesso!", Toast.LENGTH_SHORT).show()
                    manageUnitAdapter.removeItem(position)
                } else {
                    val errorMessage = response.body()?.message ?: "Erro desconhecido ao excluir unidade."
                    Toast.makeText(this@ManageUnitsActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API Error", "Falha ao excluir unidade. Código: ${response.code()}, Mensagem: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                Toast.makeText(this@ManageUnitsActivity, "Erro de conexão ao excluir unidade.", Toast.LENGTH_LONG).show()
                Log.e("API Failure", "Erro ao excluir unidade", t)
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