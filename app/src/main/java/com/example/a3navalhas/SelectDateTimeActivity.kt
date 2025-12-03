package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SelectDateTimeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerViewTimeSlots: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    private var selectedDate: String = "" // Formato: yyyy-MM-dd

    private val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    companion object {
        const val EXTRA_SELECTED_DATE_TIME = "EXTRA_SELECTED_DATE_TIME"
        const val MIN_HOUR = 9
        const val MAX_HOUR = 18
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_date_time)

        calendarView = findViewById(R.id.calendarView)
        recyclerViewTimeSlots = findViewById(R.id.recyclerViewTimeSlots)
        progressBar = findViewById(R.id.progressBarTimeSlots)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        timeSlotAdapter = TimeSlotAdapter(emptyList()) { selectedTime ->
            // Quando um horário é clicado, finaliza a Activity
            val fullDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)
            val dayMonthYear = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fullDate!!)
            val finalDateTime = "$dayMonthYear $selectedTime"

            val resultIntent = Intent().apply {
                putExtra(EXTRA_SELECTED_DATE_TIME, finalDateTime)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerViewTimeSlots.adapter = timeSlotAdapter

        // Inicializar com a data de hoje
        val today = Calendar.getInstance()
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today.time)
        fetchAvailableTimeSlots(selectedDate)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            fetchAvailableTimeSlots(selectedDate)
        }

        bottomNavigationView.selectedItemId = R.id.navigation_schedule
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, WelcomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    true
                }
                R.id.navigation_services -> {
                    startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    true
                }
                R.id.navigation_schedule -> {
                    Toast.makeText(this, "Você já está aqui", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_user -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun generateAllTimeSlots(): List<String> {
        val slots = mutableListOf<String>()
        for (hour in MIN_HOUR..MAX_HOUR) {
            slots.add(String.format("%02d:00", hour))
            if (hour < MAX_HOUR) { // Adiciona "xx:30" até 17:30
                slots.add(String.format("%02d:30", hour))
            }
        }
        return slots
    }

    private fun fetchAvailableTimeSlots(date: String) {
        progressBar.visibility = View.VISIBLE
        recyclerViewTimeSlots.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bookedSlots = api.getBookedTimeSlots(date)
                val allSlots = generateAllTimeSlots()
                val availableSlots = allSlots.filter { it !in bookedSlots }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    recyclerViewTimeSlots.visibility = View.VISIBLE
                    timeSlotAdapter.updateDataSet(availableSlots)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Log.e("SelectDateTime", "Erro ao carregar horários: ", e)
                    Toast.makeText(this@SelectDateTimeActivity, "Erro ao carregar horários.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}