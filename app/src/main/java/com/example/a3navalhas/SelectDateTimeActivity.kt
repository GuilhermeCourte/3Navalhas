package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SelectDateTimeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var calendarView: CalendarView
    private lateinit var timePicker: TimePicker
    private lateinit var buttonConfirmDateTime: MaterialButton

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDayOfMonth: Int = 0
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    companion object {
        const val REQUEST_CODE_SELECT_DATETIME = 4
        const val EXTRA_SELECTED_DATE_TIME = "EXTRA_SELECTED_DATE_TIME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_date_time)

        calendarView = findViewById(R.id.calendarView)
        timePicker = findViewById(R.id.timePicker)
        buttonConfirmDateTime = findViewById(R.id.buttonConfirmDateTime)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Inicializar com a data e hora atuais
        val calendar = Calendar.getInstance()
        selectedYear = calendar.get(Calendar.YEAR)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
        selectedMinute = calendar.get(Calendar.MINUTE)

        // Configurar TimePicker para 24h
        timePicker.setIs24HourView(true)

        // Listener para CalendarView
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month
            selectedDayOfMonth = dayOfMonth
        }

        // Listener para TimePicker
        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            selectedHour = hourOfDay
            selectedMinute = minute
        }

        // Listener para o botão de confirmação
        buttonConfirmDateTime.setOnClickListener {
            val selectedCalendar = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDayOfMonth, selectedHour, selectedMinute)
            }
            val formattedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(selectedCalendar.time)

            val resultIntent = Intent().apply {
                putExtra(EXTRA_SELECTED_DATE_TIME, formattedDateTime)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
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
                    Toast.makeText(this, "Você já está na tela de Seleção de Data/Hora", Toast.LENGTH_SHORT).show()
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