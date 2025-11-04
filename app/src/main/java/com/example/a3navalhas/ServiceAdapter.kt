package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

data class Servico(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val duration: Int,
    val imageUrl: String? = null
)

class ServiceAdapter(
    private val dataSet: List<Servico>,
    private val onItemClick: (Servico) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceIcon: ShapeableImageView = view.findViewById(R.id.serviceIcon)
        val serviceName: TextView = view.findViewById(R.id.serviceName)
        val serviceDescription: TextView = view.findViewById(R.id.serviceDescription)
        val servicePriceBadge: TextView = view.findViewById(R.id.servicePriceBadge)
        val serviceDuration: TextView = view.findViewById(R.id.serviceDuration)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_servico, viewGroup, false)
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

        viewHolder.itemView.setOnClickListener { onItemClick(servico) }
    }

    override fun getItemCount() = dataSet.size
}