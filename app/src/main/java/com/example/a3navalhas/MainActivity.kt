package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.app.AlertDialog // Import adicionado
import com.google.android.material.floatingactionbutton.FloatingActionButton // Import adicionado

class MainActivity : AppCompatActivity(), CustomAdapter.OnItemActionListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var apiService: ApiService
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fabAddProduct: FloatingActionButton // Declarar FAB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produtos)

        recyclerView = findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        // Garante que o item "Serviços" esteja selecionado ao entrar nesta Activity
        bottomNavigationView.selectedItemId = R.id.navigation_services

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
                    Toast.makeText(this, "Você já está na tela de Serviços", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_schedule -> {
                    Toast.makeText(this, "Agendar clicado", Toast.LENGTH_SHORT).show()
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

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.15.53/3navalhas_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureOkHttpClient())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        fabAddProduct = findViewById(R.id.fabAddProduct) // Encontrar o FAB
        fabAddProduct.setOnClickListener { // Configurar listener para o FAB
            val intent = Intent(this, IncluirProdutoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchProducts()
    }

    private fun fetchProducts() {
        apiService.getProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    // Passar 'this' como listener para o CustomAdapter
                    adapter = CustomAdapter(produtos.toMutableList(), this@MainActivity) 
                    recyclerView.adapter = adapter
                } else {
                    Log.e("API Error", "Falha ao carregar os produtos. Código: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Erro ao carregar os produtos.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Failure", "Erro ao carregar os produtos", t)
                Toast.makeText(this@MainActivity, "Erro de conexão ao carregar os produtos.", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Implementação da interface CustomAdapter.OnItemActionListener
    override fun onEditClick(produto: Produto) {
        val intent = Intent(this, EditarProdutoActivity::class.java).apply {
            putExtra("PRODUTO_ID", produto.PRODUTO_ID)
            // Não precisamos mais passar os outros campos, EditarProdutoActivity os buscará
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
                            Toast.makeText(this@MainActivity, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show()
                            adapter.removeItem(position) // Remover da lista localmente
                            // Ou, para ter certeza que a lista está 100% atualizada, recarregar:
                            // fetchProducts()
                        } else {
                            Toast.makeText(this@MainActivity, "Erro ao excluir produto.", Toast.LENGTH_LONG).show()
                            Log.e("API Error", "Falha ao excluir produto. Código: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Erro de conexão ao excluir produto.", Toast.LENGTH_LONG).show()
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