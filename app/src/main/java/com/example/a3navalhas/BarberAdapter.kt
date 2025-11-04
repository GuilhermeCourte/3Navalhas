package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

data class Barbeiro(
    val id: String,
    val name: String,
    val specialization: String,
    val imageUrl: String? = null,
    val unitId: String // Novo campo: ID da unidade a que o barbeiro pertence
)

class BarberAdapter(
    private val dataSet: List<Barbeiro>,
    private val onItemClick: (Barbeiro) -> Unit
) : RecyclerView.Adapter<BarberAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val barberPhoto: ShapeableImageView = view.findViewById(R.id.barberPhoto)
        val barberName: TextView = view.findViewById(R.id.barberName)
        val barberSpecialization: TextView = view.findViewById(R.id.barberSpecialization)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_barbeiro, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val barbeiro = dataSet[position]

        if (!barbeiro.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(barbeiro.imageUrl).into(viewHolder.barberPhoto)
        } else {
            viewHolder.barberPhoto.setImageResource(R.drawable.ic_person)
        }

        viewHolder.barberName.text = barbeiro.name
        viewHolder.barberSpecialization.text = barbeiro.specialization

        viewHolder.itemView.setOnClickListener { onItemClick(barbeiro) }
    }

    override fun getItemCount() = dataSet.size
}