package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class SelectBarberActivity : AppCompatActivity() {

    private lateinit var recyclerViewBarbers: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    // Constante para identificar a requisição de seleção de barbeiro
    companion object {
        const val REQUEST_CODE_SELECT_BARBER = 2
        const val EXTRA_UNIT_ID = "EXTRA_UNIT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_barber)

        recyclerViewBarbers = findViewById(R.id.recyclerViewBarbers)

        // 1. Receber o unitId da Intent
        val selectedUnitId = intent.getStringExtra(EXTRA_UNIT_ID)

        // Dados de exemplo para os barbeiros, agora com unitId
        val allBarbers = listOf(
            Barbeiro(
                id = "barber1",
                name = "João da Navalha",
                specialization = "Cortes clássicos, barba modelada",
                imageUrl = null,
                unitId = "1" // Associado à Unidade 1
            ),
            Barbeiro(
                id = "barber2",
                name = "Pedro Tesoura",
                specialization = "Modernos e degradê, coloração",
                imageUrl = null,
                unitId = "1" // Associado à Unidade 1
            ),
            Barbeiro(
                id = "barber3",
                name = "Maria Estilo",
                specialization = "Cortes femininos e infantis, tratamentos capilares",
                imageUrl = null,
                unitId = "2" // Associado à Unidade 2
            ),
            Barbeiro(
                id = "barber4",
                name = "Carlos Cabelo",
                specialization = "Especialista em kids, corte e diversão",
                imageUrl = null,
                unitId = "2" // Associado à Unidade 2
            ),
            Barbeiro(
                id = "barber5",
                name = "Ana Hair",
                specialization = "Colorimetria e tratamentos avançados",
                imageUrl = null,
                unitId = "3" // Associado à Unidade 3
            )
        )

        // 2. Filtrar barbeiros com base no unitId recebido
        val filteredBarbers = if (selectedUnitId != null) {
            allBarbers.filter { it.unitId == selectedUnitId }
        } else {
            allBarbers // Se nenhum unitId for fornecido, mostra todos (comportamento padrão)
        }

        val barberAdapter = BarberAdapter(filteredBarbers) { selectedBarber ->
            // Quando um barbeiro é clicado, retorna o ID e nome para a AgendamentoActivity
            val resultIntent = Intent().apply {
                putExtra("SELECTED_BARBER_ID", selectedBarber.id)
                putExtra("SELECTED_BARBER_NAME", selectedBarber.name)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerViewBarbers.adapter = barberAdapter

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
                    Toast.makeText(this, "Você já está na tela de Seleção de Barbeiro", Toast.LENGTH_SHORT).show()
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