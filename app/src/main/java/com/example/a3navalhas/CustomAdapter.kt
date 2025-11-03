package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CustomAdapter(
    private val dataSet: MutableList<Produto>,
    private val listener: OnItemActionListener? = null // Listener para callbacks de clique
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // Interface para callbacks de ação no item
    interface OnItemActionListener {
        fun onEditClick(produto: Produto)
        fun onDeleteClick(produto: Produto, position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceIcon: ImageView = view.findViewById(R.id.serviceIcon)
        val nome: TextView = view.findViewById(R.id.nomeProduto)
        val descricao: TextView = view.findViewById(R.id.descricaoProduto)
        val preco: TextView = view.findViewById(R.id.precoProdutoBadge)
        val duration: TextView = view.findViewById(R.id.durationTextView)
        val editIcon: ImageView = view.findViewById(R.id.editIcon) // Ícone de editar
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon) // Ícone de excluir
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

        // Configura a imagem dos ícones de editar e excluir em tempo de execução
        viewHolder.editIcon.setImageResource(R.drawable.ic_edit) // Adicionado
        viewHolder.deleteIcon.setImageResource(R.drawable.ic_delete) // Adicionado

        // Configura os OnClickListener para os ícones
        viewHolder.editIcon.setOnClickListener { listener?.onEditClick(produto) }
        viewHolder.deleteIcon.setOnClickListener { listener?.onDeleteClick(produto, position) }
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