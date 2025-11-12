package com.example.a3navalhas

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class EditBarberActivity : AppCompatActivity() {

    private lateinit var barberNameEditText: EditText
    private lateinit var barberSpecializationEditText: EditText
    private lateinit var barberImageUrlEditText: EditText
    private lateinit var unitAutoCompleteTextView: AutoCompleteTextView // Alterado
    private lateinit var updateBarberButton: Button

    private lateinit var apiService: ApiService
    private var unitList = listOf<Unidade>()
    private var selectedUnitId: String? = null
    private var barberToEdit: Barbeiro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_barber)

        barberToEdit = intent.getSerializableExtra("EXTRA_BARBER") as? Barbeiro
        if (barberToEdit == null) {
            Toast.makeText(this, "Erro: Barbeiro não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Inicializa UI
        barberNameEditText = findViewById(R.id.barberNameEditText)
        barberSpecializationEditText = findViewById(R.id.barberSpecializationEditText)
        barberImageUrlEditText = findViewById(R.id.barberImageUrlEditText)
        unitAutoCompleteTextView = findViewById(R.id.unitAutoCompleteTextView) // Alterado
        updateBarberButton = findViewById(R.id.updateBarberButton)

        populateFields()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        fetchUnitsForDropdown()

        updateBarberButton.setOnClickListener { updateBarber() }
    }

    private fun populateFields() {
        barberToEdit?.let {
            barberNameEditText.setText(it.name)
            barberSpecializationEditText.setText(it.specialization)
            barberImageUrlEditText.setText(it.imageUrl ?: "")
            selectedUnitId = it.unitId
        }
    }

    private fun fetchUnitsForDropdown() {
        apiService.getUnits().enqueue(object : Callback<List<Unidade>> {
            override fun onResponse(call: Call<List<Unidade>>, response: Response<List<Unidade>>) {
                if (response.isSuccessful) {
                    unitList = response.body() ?: emptyList()
                    val unitNames = unitList.map { it.name }
                    val adapter = ArrayAdapter(this@EditBarberActivity, android.R.layout.simple_dropdown_item_1line, unitNames)
                    unitAutoCompleteTextView.setAdapter(adapter)

                    // Pré-seleciona a unidade atual
                    barberToEdit?.let {
                        val currentUnitName = unitList.find { unit -> unit.id == it.unitId }?.name
                        if (currentUnitName != null) {
                            unitAutoCompleteTextView.setText(currentUnitName, false)
                        }
                    }

                    // Listener para quando um item é selecionado
                    unitAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                        selectedUnitId = unitList.find { it.name == parent.getItemAtPosition(position).toString() }?.id
                    }
                } else {
                    Toast.makeText(this@EditBarberActivity, "Erro ao carregar unidades", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Unidade>>, t: Throwable) {
                Toast.makeText(this@EditBarberActivity, "Falha na conexão ao carregar unidades", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBarber() {
        val name = barberNameEditText.text.toString().trim()
        val specialization = barberSpecializationEditText.text.toString().trim()
        val imageUrl = barberImageUrlEditText.text.toString().trim()
        val barberId = barberToEdit!!.id

        if (name.isEmpty() || specialization.isEmpty() || selectedUnitId == null) {
            Toast.makeText(this, "Todos os campos, exceto a imagem, são obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.updateBarber(barberId, name, specialization, if (imageUrl.isEmpty()) null else imageUrl, selectedUnitId!!).enqueue(object : Callback<ApiService.GenericResponse> {
            override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@EditBarberActivity, "Barbeiro atualizado com sucesso!", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val errorMsg = response.body()?.message ?: "Erro desconhecido"
                    Toast.makeText(this@EditBarberActivity, "Erro: $errorMsg", Toast.LENGTH_LONG).show()
                    Log.e("API Error", "Falha ao editar. Código: ${response.code()}, Msg: $errorMsg")
                }
            }
            override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                Toast.makeText(this@EditBarberActivity, "Falha na conexão", Toast.LENGTH_LONG).show()
                Log.e("API Failure", "Erro ao editar barbeiro", t)
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