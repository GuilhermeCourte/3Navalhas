package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class ManageServiceAdapter(
    private val dataSet: MutableList<Servico>,
    private val onEditClick: (Servico) -> Unit,
    private val onDeleteClick: (Servico, Int) -> Unit
) : RecyclerView.Adapter<ManageServiceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceIcon: ShapeableImageView = view.findViewById(R.id.serviceIcon)
        val serviceName: TextView = view.findViewById(R.id.serviceName)
        val serviceDescription: TextView = view.findViewById(R.id.serviceDescription)
        val servicePriceBadge: TextView = view.findViewById(R.id.servicePriceBadge)
        val serviceDuration: TextView = view.findViewById(R.id.serviceDuration)
        val buttonEditService: MaterialButton = view.findViewById(R.id.buttonEditService)
        val buttonDeleteService: MaterialButton = view.findViewById(R.id.buttonDeleteService)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_manage_service, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val servico = dataSet[position]

        if (!servico.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(servico.imageUrl).into(viewHolder.serviceIcon)
        } else {
            viewHolder.serviceIcon.setImageResource(R.drawable.ic_scissors)
        }

        viewHolder.serviceName.text = servico.name
        viewHolder.serviceDescription.text = servico.description
        viewHolder.servicePriceBadge.text = "R$ ${String.format("%.2f", servico.price)}"
        viewHolder.serviceDuration.text = "${servico.duration} min"

        viewHolder.buttonEditService.setOnClickListener { onEditClick(servico) }
        viewHolder.buttonDeleteService.setOnClickListener { onDeleteClick(servico, position) }
    }

    override fun getItemCount() = dataSet.size

    fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(servico: Servico) {
        dataSet.add(servico)
        notifyItemInserted(dataSet.size - 1)
    }

    fun updateItem(position: Int, updatedServico: Servico) {
        dataSet[position] = updatedServico
        notifyItemChanged(position)
    }

    // Novo método para atualizar o conjunto de dados
    fun updateDataSet(newDataSet: List<Servico>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }
}