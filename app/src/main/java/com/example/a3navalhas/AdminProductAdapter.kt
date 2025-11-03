package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdminProductAdapter(
    private val dataSet: MutableList<Produto>,
    private val listener: OnItemActionListener? = null // Listener para callbacks de clique
) : RecyclerView.Adapter<AdminProductAdapter.ViewHolder>() {

    // Interface para callbacks de ação no item
    interface OnItemActionListener {
        fun onEditClick(produto: Produto)
        fun onDeleteClick(produto: Produto, position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeProduto)
        val descricao: TextView = view.findViewById(R.id.descricaoProduto)
        val preco: TextView = view.findViewById(R.id.precoProduto)
        val imagem: ImageView = view.findViewById(R.id.imagemProduto) // Agora ShapeableImageView no layout
        val duration: TextView = view.findViewById(R.id.durationTextView) // Adicionado
        val editIcon: ImageView = view.findViewById(R.id.editIcon) // Ícone de editar
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon) // Ícone de excluir
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_produto_admin, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val produto = dataSet[position]
        viewHolder.nome.text = produto.PRODUTO_NOME
        viewHolder.descricao.text = produto.PRODUTO_DESC
        viewHolder.preco.text = "R$ ${produto.PRODUTO_PRECO}"
        viewHolder.duration.text = "${produto.PRODUTO_DURACAO} min" // Define a duração

        // Carrega a imagem do URL usando Picasso
        if (!produto.PRODUTO_IMAGEM.isNullOrEmpty()) {
            Picasso.get().load(produto.PRODUTO_IMAGEM).into(viewHolder.imagem)
        } else {
            // Define uma imagem padrão se a URL estiver vazia ou nula
            viewHolder.imagem.setImageResource(R.drawable.ic_barber_scissors) // Ícone padrão se não houver imagem
        }

        // Define as imagens para os ícones de editar e excluir
        viewHolder.editIcon.setImageResource(R.drawable.ic_edit) // Assumindo que ic_edit existe
        viewHolder.deleteIcon.setImageResource(R.drawable.ic_delete) // Assumindo que ic_delete existe

        // Configura os OnClickListener para os ícones
        viewHolder.editIcon.setOnClickListener { listener?.onEditClick(produto) }
        viewHolder.deleteIcon.setOnClickListener { listener?.onDeleteClick(produto, position) }
    }

    override fun getItemCount() = dataSet.size

    // Métodos para gerenciar a lista localmente
    fun removeItem(position: Int) {
        if (position >= 0 && position < dataSet.size) {
            dataSet.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateItem(produto: Produto) {
        val index = dataSet.indexOfFirst { it.PRODUTO_ID == produto.PRODUTO_ID }
        if (index != -1) {
            dataSet[index] = produto
            notifyItemChanged(index)
        }
    }
}