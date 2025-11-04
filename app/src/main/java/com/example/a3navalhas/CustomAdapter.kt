package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CustomAdapter(
    private val dataSet: MutableList<Produto>
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceIcon: ImageView = view.findViewById(R.id.serviceIcon)
        val nome: TextView = view.findViewById(R.id.nomeProduto)
        val descricao: TextView = view.findViewById(R.id.descricaoProduto)
        val preco: TextView = view.findViewById(R.id.precoProdutoBadge)
        val duration: TextView = view.findViewById(R.id.durationTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_produto, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val produto = dataSet[position]

        if (!produto.PRODUTO_IMAGEM.isNullOrEmpty()) {
            Picasso.get().load(produto.PRODUTO_IMAGEM).into(viewHolder.serviceIcon)
        } else {
            viewHolder.serviceIcon.setImageResource(R.drawable.ic_barber_scissors)
        }

        viewHolder.nome.text = produto.PRODUTO_NOME
        viewHolder.descricao.text = produto.PRODUTO_DESC
        viewHolder.preco.text = "R$ ${produto.PRODUTO_PRECO}"
        viewHolder.duration.text = "${produto.PRODUTO_DURACAO} min"
    }

    override fun getItemCount() = dataSet.size

    // Método para remover um item do dataSet (útil após exclusão no backend)
    fun removeItem(position: Int) {
        if (position >= 0 && position < dataSet.size) {
            dataSet.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Método para atualizar um item no dataSet
    fun updateItem(produto: Produto) {
        val index = dataSet.indexOfFirst { it.PRODUTO_ID == produto.PRODUTO_ID }
        if (index != -1) {
            dataSet[index] = produto
            notifyItemChanged(index)
        }
    }
}