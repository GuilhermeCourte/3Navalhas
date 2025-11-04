package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class SelectServiceActivity : AppCompatActivity() {

    private lateinit var recyclerViewServices: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    // Constante para identificar a requisição de seleção de serviço
    companion object {
        const val REQUEST_CODE_SELECT_SERVICE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_service)

        recyclerViewServices = findViewById(R.id.recyclerViewServices)

        // Dados de exemplo para os serviços
        val sampleServices = listOf(
            Servico(
                id = "service1",
                name = "Corte Social",
                description = "Clássico e discreto, com as laterais levemente mais baixas e o topo alinhado.",
                price = 40.00,
                duration = 30,
                imageUrl = null // Usará o ícone padrão
            ),
            Servico(
                id = "service2",
                name = "Barba Modelada",
                description = "Modelagem de barba com navalha e produtos premium.",
                price = 35.00,
                duration = 20,
                imageUrl = null // Usará o ícone padrão
            ),
            Servico(
                id = "service3",
                name = "Corte + Barba",
                description = "Combo completo de corte de cabelo e barba modelada.",
                price = 70.00,
                duration = 50,
                imageUrl = null // Usará o ícone padrão
            )
        )

        val serviceAdapter = ServiceAdapter(sampleServices) { selectedService ->
            // Quando um serviço é clicado, retorna o ID e nome para a AgendamentoActivity
            val resultIntent = Intent().apply {
                putExtra("SELECTED_SERVICE_ID", selectedService.id)
                putExtra("SELECTED_SERVICE_NAME", selectedService.name)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerViewServices.adapter = serviceAdapter

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        // Garante que o item "Agendar" esteja selecionado ao entrar nesta Activity
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
                    Toast.makeText(this, "Você já está na tela de Seleção de Serviço", Toast.LENGTH_SHORT).show()
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
}