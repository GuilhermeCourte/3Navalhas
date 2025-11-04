package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class AgendamentoActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var buttonSelectUnit: MaterialButton
    private lateinit var buttonSelectBarber: MaterialButton
    private lateinit var buttonSelectService: MaterialButton
    private lateinit var buttonSelectDateTime: MaterialButton

    private var selectedUnitId: String? = null
    private var selectedBarberId: String? = null
    private var selectedServiceId: String? = null
    private var selectedDateTime: String? = null

    companion object {
        const val REQUEST_CODE_SELECT_UNIT = 1
        const val REQUEST_CODE_SELECT_BARBER = 2
        const val REQUEST_CODE_SELECT_SERVICE = 3
        const val REQUEST_CODE_SELECT_DATETIME = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendamento)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        buttonSelectUnit = findViewById(R.id.buttonSelectUnit)
        buttonSelectBarber = findViewById(R.id.buttonSelectBarber)
        buttonSelectService = findViewById(R.id.buttonSelectService)
        buttonSelectDateTime = findViewById(R.id.buttonSelectDateTime)

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
            val intent = Intent(this, SelectBarberActivity::class.java).apply {
                putExtra(SelectBarberActivity.EXTRA_UNIT_ID, selectedUnitId)
            }
            startActivityForResult(intent, REQUEST_CODE_SELECT_BARBER)
        }

        buttonSelectService.setOnClickListener {
            val intent = Intent(this, SelectServiceActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_SERVICE)
        }

        // Configurar listener para o botão "SELECIONAR DATA E HORA"
        buttonSelectDateTime.setOnClickListener {
            val intent = Intent(this, SelectDateTimeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_DATETIME)
        }
    }

    @Deprecated("Deprecated in API 33, mas ainda funcional para este caso")
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
}