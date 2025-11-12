package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class ManageBarberAdapter(
    private val dataSet: MutableList<Barbeiro>,
    private val onEditClick: (Barbeiro) -> Unit,
    private val onDeleteClick: (Barbeiro, Int) -> Unit
) : RecyclerView.Adapter<ManageBarberAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val barberPhoto: ShapeableImageView = view.findViewById(R.id.barberPhoto)
        val barberName: TextView = view.findViewById(R.id.barberName)
        val barberSpecialization: TextView = view.findViewById(R.id.barberSpecialization)
        val barberUnit: TextView = view.findViewById(R.id.barberUnit)
        val buttonEditBarber: MaterialButton = view.findViewById(R.id.buttonEditBarber)
        val buttonDeleteBarber: MaterialButton = view.findViewById(R.id.buttonDeleteBarber)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_manage_barber, viewGroup, false)
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
        viewHolder.barberUnit.text = "Unidade: ${barbeiro.unitName ?: "Não especificada"}" // Alterado para exibir o nome da unidade

        viewHolder.buttonEditBarber.setOnClickListener { onEditClick(barbeiro) }
        viewHolder.buttonDeleteBarber.setOnClickListener { onDeleteClick(barbeiro, position) }
    }

    override fun getItemCount() = dataSet.size

    fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(barbeiro: Barbeiro) {
        dataSet.add(barbeiro)
        notifyItemInserted(dataSet.size - 1)
    }

    fun updateItem(position: Int, updatedBarbeiro: Barbeiro) {
        dataSet[position] = updatedBarbeiro
        notifyItemChanged(position)
    }

    // Novo método para atualizar o conjunto de dados
    fun updateDataSet(newDataSet: List<Barbeiro>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }
}