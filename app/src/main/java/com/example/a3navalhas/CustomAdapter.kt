package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso // Picasso É necessário para este adapter

class CustomAdapter(private val dataSet: MutableList<Produto>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceIcon: ImageView = view.findViewById(R.id.serviceIcon)
        val nome: TextView = view.findViewById(R.id.nomeProduto)
        val descricao: TextView = view.findViewById(R.id.descricaoProduto)
        val preco: TextView = view.findViewById(R.id.precoProdutoBadge) // ID atualizado
        val duration: TextView = view.findViewById(R.id.durationTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_produto, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val produto = dataSet[position]

        // Carrega a imagem do URL usando Picasso
        if (!produto.PRODUTO_IMAGEM.isNullOrEmpty()) {
            Picasso.get().load(produto.PRODUTO_IMAGEM).into(viewHolder.serviceIcon)
        } else {
            // Define uma imagem padrão se a URL estiver vazia ou nula
            viewHolder.serviceIcon.setImageResource(R.drawable.ic_barber_scissors)
        }

        viewHolder.nome.text = produto.PRODUTO_NOME
        viewHolder.descricao.text = produto.PRODUTO_DESC
        viewHolder.preco.text = "R$ ${produto.PRODUTO_PRECO}"
        viewHolder.duration.text = "${produto.PRODUTO_DURACAO} min"
    }

    override fun getItemCount() = dataSet.size
}