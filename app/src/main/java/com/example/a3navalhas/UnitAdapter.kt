package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

data class Unidade(
    val id: String,
    val name: String,
    val cityState: String,
    val addressCep: String,
    val imageUrl: String? = null
)

class UnitAdapter(
    private val dataSet: List<Unidade>,
    private val onItemClick: (Unidade) -> Unit
) : RecyclerView.Adapter<UnitAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val unitLogo: ShapeableImageView = view.findViewById(R.id.unitLogo)
        val unitName: TextView = view.findViewById(R.id.unitName)
        val unitCityState: TextView = view.findViewById(R.id.unitCityState)
        val unitAddressCep: TextView = view.findViewById(R.id.unitAddressCep)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_unidade, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val unidade = dataSet[position]

        if (!unidade.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(unidade.imageUrl).into(viewHolder.unitLogo)
        } else {
            viewHolder.unitLogo.setImageResource(R.drawable.logo_tres_navalhas)
        }

        viewHolder.unitName.text = unidade.name
        viewHolder.unitCityState.text = unidade.cityState
        viewHolder.unitAddressCep.text = unidade.addressCep

        viewHolder.itemView.setOnClickListener { onItemClick(unidade) }
    }

    override fun getItemCount() = dataSet.size
}