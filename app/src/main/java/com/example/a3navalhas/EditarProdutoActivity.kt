    package com.example.a3navalhas

    import android.os.Bundle
    import android.util.Log
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.util.concurrent.TimeUnit
    import okhttp3.OkHttpClient // Adicionado
    import okhttp3.logging.HttpLoggingInterceptor // Adicionado

    class EditarProdutoActivity : AppCompatActivity() {
        private lateinit var nomeEditText: EditText
        private lateinit var descricaoEditText: EditText
        private lateinit var precoEditText: EditText
        private lateinit var imagemEditText: EditText
        private lateinit var duracaoEditText: EditText
        private lateinit var salvarButton: Button
        private var produtoId: Int = 0
        private lateinit var apiService: ApiService // Declarar aqui

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_editar_produto)

            nomeEditText = findViewById(R.id.nomeEditText)
            descricaoEditText = findViewById(R.id.descricaoEditText)
            precoEditText = findViewById(R.id.precoEditText)
            imagemEditText = findViewById(R.id.imagemEditText)
            duracaoEditText = findViewById(R.id.duracaoEditText)
            salvarButton = findViewById(R.id.salvarButton)

            // Resgatar apenas o ID do produto da Intent
            produtoId = intent.getIntExtra("PRODUTO_ID", 0)

            // Configuração do Retrofit (agora dentro do onCreate para ser acessível)
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.15.53/3navalhas_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(configureOkHttpClient()) // Usar o OkHttpClient configurado
                .build()
            apiService = retrofit.create(ApiService::class.java)

            // Carregar os detalhes mais recentes do produto
            fetchProductDetails(produtoId)

            salvarButton.setOnClickListener {
                val produtoAtualizado = Produto(
                    produtoId,
                    nomeEditText.text.toString(),
                    descricaoEditText.text.toString(),
                    precoEditText.text.toString(),
                    "0", // PRODUTO_DESCONTO (valor padrão)
                    1, // CATEGORIA_ID (valor padrão)
                    1, // PRODUTO_ATIVO (valor padrão)
                    imagemEditText.text.toString(),
                    duracaoEditText.text.toString()
                )

                apiService.editarProduto(
                    produtoAtualizado.PRODUTO_ID,
                    produtoAtualizado.PRODUTO_NOME,
                    produtoAtualizado.PRODUTO_DESC,
                    produtoAtualizado.PRODUTO_PRECO,
                    produtoAtualizado.PRODUTO_IMAGEM,
                    produtoAtualizado.PRODUTO_DURACAO
                ).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@EditarProdutoActivity, "Produto atualizado com sucesso!", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@EditarProdutoActivity, "Erro na atualização", Toast.LENGTH_LONG).show()
                            Log.e("API Error", "Falha ao atualizar produto. Código: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@EditarProdutoActivity, "Erro ao atualizar o produto", Toast.LENGTH_LONG).show()
                        Log.e("API Failure", "Erro ao atualizar o produto", t)
                    }
                })
            }
        }

        private fun fetchProductDetails(id: Int) {
            apiService.getProdutoById(id).enqueue(object : Callback<Produto> {
                override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                    if (response.isSuccessful) {
                        val produto = response.body()
                        produto?.let {
                            nomeEditText.setText(it.PRODUTO_NOME)
                            descricaoEditText.setText(it.PRODUTO_DESC)
                            precoEditText.setText(it.PRODUTO_PRECO)
                            imagemEditText.setText(it.PRODUTO_IMAGEM)
                            duracaoEditText.setText(it.PRODUTO_DURACAO) // Preenche a duração
                        }
                    } else {
                        Toast.makeText(this@EditarProdutoActivity, "Erro ao carregar detalhes do produto.", Toast.LENGTH_LONG).show()
                        Log.e("API Error", "Falha ao carregar detalhes do produto. Código: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Produto>, t: Throwable) {
                    Toast.makeText(this@EditarProdutoActivity, "Erro de conexão ao carregar detalhes do produto.", Toast.LENGTH_LONG).show()
                    Log.e("API Failure", "Erro ao carregar detalhes do produto", t)
                }
            })
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