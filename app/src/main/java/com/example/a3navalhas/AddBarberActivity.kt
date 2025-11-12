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

class AddBarberActivity : AppCompatActivity() {

    private lateinit var barberNameEditText: EditText
    private lateinit var barberSpecializationEditText: EditText
    private lateinit var barberImageUrlEditText: EditText
    private lateinit var unitAutoCompleteTextView: AutoCompleteTextView // Alterado de Spinner
    private lateinit var saveBarberButton: Button

    private lateinit var apiService: ApiService
    private var unitList = listOf<Unidade>()
    private var selectedUnitId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_barber)

        // Inicializa UI
        barberNameEditText = findViewById(R.id.barberNameEditText)
        barberSpecializationEditText = findViewById(R.id.barberSpecializationEditText)
        barberImageUrlEditText = findViewById(R.id.barberImageUrlEditText)
        unitAutoCompleteTextView = findViewById(R.id.unitAutoCompleteTextView) // ID Alterado
        saveBarberButton = findViewById(R.id.saveBarberButton)

        // Configura Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.171.29.115/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        fetchUnitsForDropdown()

        saveBarberButton.setOnClickListener { saveBarber() }
    }

    private fun fetchUnitsForDropdown() {
        apiService.getUnits().enqueue(object : Callback<List<Unidade>> {
            override fun onResponse(call: Call<List<Unidade>>, response: Response<List<Unidade>>) {
                if (response.isSuccessful) {
                    unitList = response.body() ?: emptyList()
                    val unitNames = unitList.map { it.name }

                    val adapter = ArrayAdapter(this@AddBarberActivity, android.R.layout.simple_dropdown_item_1line, unitNames)
                    unitAutoCompleteTextView.setAdapter(adapter)

                    // Listener para quando um item é selecionado
                    unitAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                        // A posição corresponde diretamente à lista `unitNames`
                        selectedUnitId = unitList.find { it.name == parent.getItemAtPosition(position).toString() }?.id
                    }
                } else {
                    Toast.makeText(this@AddBarberActivity, "Erro ao carregar unidades", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Unidade>>, t: Throwable) {
                Toast.makeText(this@AddBarberActivity, "Falha na conexão ao carregar unidades", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveBarber() {
        val name = barberNameEditText.text.toString().trim()
        val specialization = barberSpecializationEditText.text.toString().trim()
        val imageUrl = barberImageUrlEditText.text.toString().trim()

        if (name.isEmpty() || specialization.isEmpty()) {
            Toast.makeText(this, "Nome e especialização são obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedUnitId == null) {
            Toast.makeText(this, "Selecione uma unidade", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.addBarber(name, specialization, if (imageUrl.isEmpty()) null else imageUrl, selectedUnitId!!).enqueue(object : Callback<ApiService.GenericResponse> {
            override fun onResponse(call: Call<ApiService.GenericResponse>, response: Response<ApiService.GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AddBarberActivity, "Barbeiro adicionado com sucesso!", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val errorMessage = response.body()?.message ?: "Erro desconhecido"
                    Toast.makeText(this@AddBarberActivity, "Erro: $errorMessage", Toast.LENGTH_LONG).show()
                    Log.e("API Error", "Falha ao add barbeiro. Código: ${response.code()}, Msg: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<ApiService.GenericResponse>, t: Throwable) {
                Toast.makeText(this@AddBarberActivity, "Falha na conexão", Toast.LENGTH_LONG).show()
                Log.e("API Failure", "Erro ao add barbeiro", t)
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