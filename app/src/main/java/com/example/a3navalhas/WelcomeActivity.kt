package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class WelcomeActivity : AppCompatActivity() {

    private lateinit var scheduleNowButton: MaterialButton
    private lateinit var viewServicesButton: MaterialButton
    private lateinit var viewReviewsButton: MaterialButton
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        scheduleNowButton = findViewById(R.id.scheduleNowButton)
        viewServicesButton = findViewById(R.id.viewServicesButton)
        viewReviewsButton = findViewById(R.id.viewReviewsButton)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Configurar listener para o botão "AGENDAR AGORA"
        scheduleNowButton.setOnClickListener {
            val intent = Intent(this, AgendamentoActivity::class.java)
            startActivity(intent)
        }

        // Configurar listener para o botão "VER SERVIÇOS" (leva para MainActivity)
        viewServicesButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Adicionar flags para limpar o back stack e tratar MainActivity como a nova raiz da navegação.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // Finaliza WelcomeActivity para que MainActivity seja a nova tela inicial do app
        }

        // Configurar listener para o botão "VER AVALIAÇÕES"
        viewReviewsButton.setOnClickListener {
            Toast.makeText(this, "Ver Avaliações clicado", Toast.LENGTH_SHORT).show()
            // Implementar navegação para a tela de avaliações
        }

        // Configurar o listener para a Bottom Navigation View
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    Toast.makeText(this, "Início clicado", Toast.LENGTH_SHORT).show()
                    // Já estamos na tela inicial, não faz nada ou atualiza se necessário.
                    true
                }
                R.id.navigation_services -> {
                    val intent = Intent(this, MainActivity::class.java)
                    // Adicionar flags para limpar o back stack e tratar MainActivity como a nova raiz da navegação.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish() // Finaliza WelcomeActivity para que MainActivity seja a nova tela inicial do app
                    true
                }
                R.id.navigation_schedule -> {
                    Toast.makeText(this, "Agendar clicado", Toast.LENGTH_SHORT).show()
                    // Implementar navegação para a tela de agendamento
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