package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class ServiceDisplayAdapter(
    private val dataSet: MutableList<Servico>
) : RecyclerView.Adapter<ServiceDisplayAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceIcon: ShapeableImageView = view.findViewById(R.id.serviceIcon) // Usando ShapeableImageView
        val serviceName: TextView = view.findViewById(R.id.serviceName)
        val serviceDescription: TextView = view.findViewById(R.id.serviceDescription)
        val servicePriceBadge: TextView = view.findViewById(R.id.servicePriceBadge)
        val serviceDuration: TextView = view.findViewById(R.id.serviceDuration)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_servico, viewGroup, false) // Inflar item_servico.xml
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
    }

    override fun getItemCount() = dataSet.size

    // Método para atualizar o conjunto de dados
    fun updateDataSet(newDataSet: List<Servico>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }
}