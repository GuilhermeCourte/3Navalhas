package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class SelectUnitActivity : AppCompatActivity() {

    private lateinit var recyclerViewUnits: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_unit)

        recyclerViewUnits = findViewById(R.id.recyclerViewUnits)

        // Dados de exemplo para as unidades
        val sampleUnits = listOf(
            Unidade(
                id = "1",
                name = "TRÊS NAVALHAS I - CAPÃO REDONDO",
                cityState = "São Paulo / SP",
                addressCep = "Avenida Carlos Caldeira Filho, 1808 - 05464-187",
                imageUrl = null // Usará o logo padrão
            ),
            Unidade(
                id = "2",
                name = "TRÊS NAVALHAS II - PARQUE REGINA",
                cityState = "São Paulo / SP",
                addressCep = "Rua Francisco Soares, 106 - 05774-300",
                imageUrl = null // Usará o logo padrão
            ),
            Unidade(
                id = "3",
                name = "TRÊS NAVALHAS III - VILA ANDRADE",
                cityState = "São Paulo / SP",
                addressCep = "Rua Wilson, 685 - 05665-030",
                imageUrl = null // Usará o logo padrão
            )
        )

        val unitAdapter = UnitAdapter(sampleUnits) { selectedUnit ->
            // Quando uma unidade é clicada, retorna o ID para a AgendamentoActivity
            val resultIntent = Intent().apply {
                putExtra("SELECTED_UNIT_ID", selectedUnit.id)
                putExtra("SELECTED_UNIT_NAME", selectedUnit.name)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerViewUnits.adapter = unitAdapter

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
                    Toast.makeText(this, "Você já está na tela de Seleção de Unidade", Toast.LENGTH_SHORT).show()
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