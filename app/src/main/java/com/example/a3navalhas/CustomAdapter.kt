package com.example.a3navalhas

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomAdapter(private val dataSet: MutableList<Produto>, private val apiService: ApiService) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeProduto)
        val descricao: TextView = view.findViewById(R.id.descricaoProduto)
        val preco: TextView = view.findViewById(R.id.precoProduto)
        val imagem: ImageView = view.findViewById(R.id.imagemProduto)
        val editarButton: Button = view.findViewById(R.id.editarButton)
        val deletarButton: Button = view.findViewById(R.id.deletarButton)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_produto, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val produto = dataSet[position]
        viewHolder.nome.text = produto.PRODUTO_NOME
        viewHolder.descricao.text = produto.PRODUTO_DESC
        viewHolder.preco.text = "R$ ${produto.PRODUTO_PRECO}"
        Picasso.get().load(produto.PRODUTO_IMAGEM).into(viewHolder.imagem)

        viewHolder.editarButton.setOnClickListener {
            val context = it.context
            val intent = Intent(context, EditarProdutoActivity::class.java).apply {
                putExtra("PRODUTO_ID", produto.PRODUTO_ID)
                putExtra("PRODUTO_NOME", produto.PRODUTO_NOME)
                putExtra("PRODUTO_DESC", produto.PRODUTO_DESC)
                putExtra("PRODUTO_PRECO", produto.PRODUTO_PRECO)
                putExtra("PRODUTO_IMAGEM", produto.PRODUTO_IMAGEM)
            }
            context.startActivity(intent)
        }

        viewHolder.deletarButton.setOnClickListener {
            apiService.deletarProduto(produto.PRODUTO_ID).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(it.context, "Produto deletado com sucesso!", Toast.LENGTH_LONG).show()
                        dataSet.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, dataSet.size)
                    } else {
                        Toast.makeText(it.context, "Erro ao deletar o produto", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(it.context, "Falha na comunicação: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun getItemCount() = dataSet.size
}
