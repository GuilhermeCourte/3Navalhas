package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText // Importar TextInputEditText
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var emailPhoneEditText: TextInputEditText // Mudado para emailPhoneEditText
    private lateinit var passwordEditText: TextInputEditText // Mudado para TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailPhoneEditText = findViewById(R.id.emailPhoneEditText) // Usar o novo ID
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = emailPhoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        val logging = HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.15.9/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.login(email, password)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@LoginActivity, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, AdminActivity::class.java) // Redirecionar para AdminActivity
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.body()?.message ?: "Usuário ou senha inválidos"
                    Log.e("LoginError", "Código: ${response.code()}, Resposta: $errorBody")
                    Toast.makeText(this@LoginActivity, "Falha no login: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginFailure", "Erro de conexão", t)
                Toast.makeText(this@LoginActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}