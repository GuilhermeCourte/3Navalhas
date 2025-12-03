package com.example.a3navalhas

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import com.example.a3navalhas.AgendamentoRequest 
import com.example.a3navalhas.ApiResponse 

class AgendamentoActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var buttonSelectUnit: MaterialButton
    private lateinit var buttonSelectBarber: MaterialButton
    private lateinit var buttonSelectService: MaterialButton
    private lateinit var buttonSelectDateTime: MaterialButton
    private lateinit var buttonConfirm: MaterialButton
    private lateinit var buttonViewMyAppointments: MaterialButton // NOVO: Botão Meus Agendamentos
    private lateinit var progressBar: ProgressBar // Referência ao ProgressBar

    private var selectedUnitId: String? = null
    private var selectedBarberId: String? = null
    private var selectedServiceId: String? = null
    private var selectedDateTime: String? = null // Formato: dd/MM/yyyy HH:mm

    private val api: ApiService by lazy {
        // Configurar o Logging Interceptor
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        // Adicionar o interceptor ao OkHttpClient
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // Usando a constante centralizada
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient) // Adicionar o cliente HTTP com o interceptor
            .build()
            .create(ApiService::class.java)
    }

    companion object {
        const val REQUEST_CODE_SELECT_UNIT = 1
        const val REQUEST_CODE_SELECT_BARBER = 2
        const val REQUEST_CODE_SELECT_SERVICE = 3
        const val REQUEST_CODE_SELECT_DATETIME = 4
        const val EXTRA_USER_PHONE = "user_phone" // NOVO: Chave para passar o telefone
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendamento)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        buttonSelectUnit = findViewById(R.id.buttonSelectUnit)
        buttonSelectBarber = findViewById(R.id.buttonSelectBarber)
        buttonSelectService = findViewById(R.id.buttonSelectService)
        buttonSelectDateTime = findViewById(R.id.buttonSelectDateTime)
        buttonConfirm = findViewById(R.id.buttonConfirm)
        buttonViewMyAppointments = findViewById(R.id.buttonViewMyAppointments) // Inicializa o novo botão
        progressBar = findViewById(R.id.progressBar) // Inicializa o ProgressBar

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
                    Toast.makeText(this, "Você já está na tela de Agendamento", Toast.LENGTH_SHORT).show()
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

        buttonSelectUnit.setOnClickListener {
            val intent = Intent(this, SelectUnitActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_UNIT)
        }

        buttonSelectBarber.setOnClickListener {
            if (selectedUnitId == null) {
                Toast.makeText(this, "Por favor, selecione uma unidade primeiro.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, SelectBarberActivity::class.java).apply {
                putExtra(SelectBarberActivity.EXTRA_UNIT_ID, selectedUnitId)
            }
            startActivityForResult(intent, REQUEST_CODE_SELECT_BARBER)
        }

        buttonSelectService.setOnClickListener {
            val intent = Intent(this, SelectServiceActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_SERVICE)
        }

        buttonSelectDateTime.setOnClickListener {
            val intent = Intent(this, SelectDateTimeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_DATETIME)
        }

        buttonConfirm.setOnClickListener {
            validateAndShowConfirmModal()
        }

        // NOVO LISTENER PARA O BOTÃO MEUS AGENDAMENTOS
        buttonViewMyAppointments.setOnClickListener {
            val sharedPreferences = getSharedPreferences("3NavalhasPrefs", Context.MODE_PRIVATE)
            val userPhone = sharedPreferences.getString("USER_PHONE", null)

            if (userPhone.isNullOrEmpty()) {
                Toast.makeText(this, "Por favor, agende um horário primeiro para ver seus agendamentos.", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, UserAppointmentsActivity::class.java).apply {
                    putExtra(EXTRA_USER_PHONE, userPhone)
                }
                startActivity(intent)
            }
        }
    }

    @Deprecated("Deprecated em API 33, mas ainda funcional para este caso")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SELECT_UNIT -> {
                    val newSelectedUnitId = data?.getStringExtra("SELECTED_UNIT_ID")
                    val newSelectedUnitName = data?.getStringExtra("SELECTED_UNIT_NAME")

                    if (newSelectedUnitId != selectedUnitId) {
                        selectedUnitId = newSelectedUnitId
                        buttonSelectUnit.text = newSelectedUnitName ?: "SELECIONAR UNIDADE"
                        Toast.makeText(this, "Unidade Selecionada: ${newSelectedUnitName ?: "Nenhuma"} (ID: $selectedUnitId)", Toast.LENGTH_LONG).show()
                        
                        // Resetar seleções dependentes
                        selectedBarberId = null
                        buttonSelectBarber.text = "SELECIONAR BARBEIRO"
                        selectedServiceId = null
                        buttonSelectService.text = "SELECIONAR SERVIÇO"
                        selectedDateTime = null
                        buttonSelectDateTime.text = "SELECIONAR DATA E HORA"
                        Toast.makeText(this, "Campos de Barbeiro, Serviço e Data/Hora resetados.", Toast.LENGTH_SHORT).show()
                    } else if (!newSelectedUnitName.isNullOrEmpty()) {
                        buttonSelectUnit.text = newSelectedUnitName
                        Toast.makeText(this, "Unidade Selecionada: $newSelectedUnitName (ID: $selectedUnitId)", Toast.LENGTH_LONG).show()
                    }
                }
                REQUEST_CODE_SELECT_BARBER -> {
                    selectedBarberId = data?.getStringExtra("SELECTED_BARBER_ID")
                    val selectedBarberName = data?.getStringExtra("SELECTED_BARBER_NAME")

                    if (!selectedBarberName.isNullOrEmpty()) {
                        buttonSelectBarber.text = selectedBarberName
                        Toast.makeText(this, "Barbeiro Selecionado: $selectedBarberName (ID: $selectedBarberId)", Toast.LENGTH_LONG).show()
                    } else {
                        selectedBarberId = null
                        buttonSelectBarber.text = "SELECIONAR BARBEIRO"
                        Toast.makeText(this, "Seleção de Barbeiro cancelada.", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_CODE_SELECT_SERVICE -> {
                    selectedServiceId = data?.getStringExtra("SELECTED_SERVICE_ID")
                    val selectedServiceName = data?.getStringExtra("SELECTED_SERVICE_NAME")

                    if (!selectedServiceName.isNullOrEmpty()) {
                        buttonSelectService.text = selectedServiceName
                        Toast.makeText(this, "Serviço Selecionado: $selectedServiceName (ID: $selectedServiceId)", Toast.LENGTH_LONG).show()
                    } else {
                        selectedServiceId = null
                        buttonSelectService.text = "SELECIONAR SERVIÇO"
                        Toast.makeText(this, "Seleção de Serviço cancelada.", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_CODE_SELECT_DATETIME -> {
                    selectedDateTime = data?.getStringExtra(SelectDateTimeActivity.EXTRA_SELECTED_DATE_TIME)

                    if (!selectedDateTime.isNullOrEmpty()) {
                        buttonSelectDateTime.text = selectedDateTime
                        Toast.makeText(this, "Data e Hora Selecionadas: $selectedDateTime", Toast.LENGTH_LONG).show()
                    } else {
                        selectedDateTime = null
                        buttonSelectDateTime.text = "SELECIONAR DATA E HORA"
                        Toast.makeText(this, "Seleção de Data e Hora cancelada.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateAndShowConfirmModal() {
        if (selectedUnitId.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, selecione uma unidade.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedBarberId.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, selecione um barbeiro.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedServiceId.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, selecione um serviço.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedDateTime.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, selecione uma data e hora.", Toast.LENGTH_SHORT).show()
            return
        }
        showConfirmAppointmentModal()
    }

    private fun showConfirmAppointmentModal() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_appointment, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        val editTextName = dialogView.findViewById<TextInputEditText>(R.id.editTextName)
        val editTextPhone = dialogView.findViewById<TextInputEditText>(R.id.editTextPhone)
        val buttonCancel = dialogView.findViewById<MaterialButton>(R.id.buttonCancel)
        val buttonDialogConfirm = dialogView.findViewById<MaterialButton>(R.id.buttonDialogConfirm)

        // Pré-popular campos com dados do usuário salvos, se existirem
        val sharedPreferences = getSharedPreferences("3NavalhasPrefs", Context.MODE_PRIVATE)
        editTextName.setText(sharedPreferences.getString("USER_NAME", ""))
        editTextPhone.setText(sharedPreferences.getString("USER_PHONE", ""))

        buttonCancel.setOnClickListener { alertDialog.dismiss() }
        buttonDialogConfirm.setOnClickListener {
            val nome = editTextName.text.toString().trim()
            val telefone = editTextPhone.text.toString().trim()

            if (nome.isEmpty()) {
                editTextName.error = "Nome é obrigatório"
                return@setOnClickListener
            }
            if (telefone.isEmpty()) {
                editTextPhone.error = "Telefone é obrigatório"
                return@setOnClickListener
            }

            // Tudo validado, fazer chamada à API
            alertDialog.dismiss() // Fecha o modal imediatamente para mostrar o ProgressBar da Activity
            progressBar.visibility = View.VISIBLE

            // Parsear data e hora
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val parsedDateTime = dateTimeFormat.parse(selectedDateTime!!)
            val dataAgendamento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDateTime!!)
            val horaAgendamento = SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsedDateTime)

            val request = AgendamentoRequest(
                nome_cliente = nome,
                telefone_cliente = telefone,
                data_agendamento = dataAgendamento,
                hora_agendamento = horaAgendamento
                // Adicionar IDs de unidade, barbeiro, serviço se a API precisar
                // unit_id = selectedUnitId!!,
                // barber_id = selectedBarberId!!,
                // service_id = selectedServiceId!!
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = api.criarAgendamento(request)
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        if (response.sucesso) {
                            sharedPreferences.edit().apply {
                                putString("USER_NAME", nome)
                                putString("USER_PHONE", telefone)
                                apply()
                            }
                            Toast.makeText(this@AgendamentoActivity, response.mensagem, Toast.LENGTH_LONG).show()
                            // Redirecionar para a AdminActivity ou tela inicial
                            val intent = Intent(this@AgendamentoActivity, WelcomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@AgendamentoActivity, "Erro ao agendar: ${response.erro}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Log.e("AgendamentoActivity", "Erro na chamada da API: ", e)
                        Toast.makeText(this@AgendamentoActivity, "Falha na conexão ou erro do servidor.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
