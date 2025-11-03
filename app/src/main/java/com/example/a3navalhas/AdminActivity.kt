package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast // Import adicionado para Toasts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.app.AlertDialog // Import adicionado para AlertDialog

class AdminActivity : AppCompatActivity(), AdminProductAdapter.OnItemActionListener {

    private lateinit var adminRecyclerView: RecyclerView
    private lateinit var adminProductAdapter: AdminProductAdapter
    private lateinit var adminAddProductButton: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        adminRecyclerView = findViewById(R.id.adminRecyclerViewProdutos)
        adminRecyclerView.layoutManager = LinearLayoutManager(this)
        adminAddProductButton = findViewById(R.id.adminIncluirProdutoButton)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.15.9/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        adminAddProductButton.setOnClickListener {
            val intent = Intent(this, IncluirProdutoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAdminProducts()
    }

    private fun fetchAdminProducts() {
        apiService.getProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    // Passar 'this' como listener para o AdminProductAdapter
                    adminProductAdapter = AdminProductAdapter(produtos.toMutableList(), this@AdminActivity)
                    adminRecyclerView.adapter = adminProductAdapter
                } else {
                    Log.e("API Admin Error", "Falha ao carregar os produtos do admin. Código: ${response.code()}")
                    Toast.makeText(this@AdminActivity, "Erro ao carregar os produtos do admin.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Admin Failure", "Erro ao carregar os produtos do admin", t)
                Toast.makeText(this@AdminActivity, "Erro de conexão ao carregar os produtos do admin.", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Implementação da interface AdminProductAdapter.OnItemActionListener
    override fun onEditClick(produto: Produto) {
        val intent = Intent(this, EditarProdutoActivity::class.java).apply {
            putExtra("PRODUTO_ID", produto.PRODUTO_ID)
        }
        startActivity(intent)
    }

    override fun onDeleteClick(produto: Produto, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja excluir o produto '${produto.PRODUTO_NOME}'?")
            .setPositiveButton("Sim") { dialog, which ->
                apiService.deletarProduto(produto.PRODUTO_ID).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminActivity, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show()
                            adminProductAdapter.removeItem(position) // Remover da lista localmente
                        } else {
                            Toast.makeText(this@AdminActivity, "Erro ao excluir produto.", Toast.LENGTH_LONG).show()
                            Log.e("API Error", "Falha ao excluir produto. Código: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@AdminActivity, "Erro de conexão ao excluir produto.", Toast.LENGTH_LONG).show()
                        Log.e("API Failure", "Erro ao excluir produto", t)
                    }
                })
            }
            .setNegativeButton("Não", null)
            .show()
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