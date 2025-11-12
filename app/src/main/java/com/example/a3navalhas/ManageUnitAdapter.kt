package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class ManageUnitAdapter(
    private val dataSet: MutableList<Unidade>,
    private val onEditClick: (Unidade) -> Unit,
    private val onDeleteClick: (Unidade, Int) -> Unit
) : RecyclerView.Adapter<ManageUnitAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val unitLogo: ShapeableImageView = view.findViewById(R.id.unitLogo)
        val unitName: TextView = view.findViewById(R.id.unitName)
        val unitCityState: TextView = view.findViewById(R.id.unitCityState)
        val unitAddressCep: TextView = view.findViewById(R.id.unitAddressCep)
        val buttonEditUnit: MaterialButton = view.findViewById(R.id.buttonEditUnit)
        val buttonDeleteUnit: MaterialButton = view.findViewById(R.id.buttonDeleteUnit)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_manage_unit, viewGroup, false)
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

        viewHolder.buttonEditUnit.setOnClickListener { onEditClick(unidade) }
        viewHolder.buttonDeleteUnit.setOnClickListener { onDeleteClick(unidade, position) }
    }

    override fun getItemCount() = dataSet.size

    fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(unidade: Unidade) {
        dataSet.add(unidade)
        notifyItemInserted(dataSet.size - 1)
    }

    fun updateItem(position: Int, updatedUnidade: Unidade) {
        dataSet[position] = updatedUnidade
        notifyItemChanged(position)
    }

    // Novo método para atualizar o conjunto de dados
    fun updateDataSet(newDataSet: List<Unidade>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }
}