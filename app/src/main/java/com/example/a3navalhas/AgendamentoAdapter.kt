package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class AgendamentoAdapter(
    private val dataSet: MutableList<Agendamento>,
    private val onDeleteClick: (Agendamento, Int) -> Unit, // Callback para clique no botão de exclusão
    private val showDeleteButton: Boolean // NOVO: Parâmetro para controlar a visibilidade do botão
) : RecyclerView.Adapter<AgendamentoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewClientName: TextView = view.findViewById(R.id.textViewClientName)
        val textViewAppointmentDateTime: TextView = view.findViewById(R.id.textViewAppointmentDateTime)
        val buttonDeleteAppointment: MaterialButton = view.findViewById(R.id.buttonDeleteAppointment)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_agendamento, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val agendamento = dataSet[position]
        viewHolder.textViewClientName.text = "Nome do Cliente: ${agendamento.nome_cliente}"
        viewHolder.textViewAppointmentDateTime.text = "Data e Hora: ${agendamento.data_agendamento} - ${agendamento.hora_agendamento}"

        // NOVO: Controlar a visibilidade do botão de exclusão
        if (showDeleteButton) {
            viewHolder.buttonDeleteAppointment.visibility = View.VISIBLE
            viewHolder.buttonDeleteAppointment.setOnClickListener {
                onDeleteClick(agendamento, position)
            }
        } else {
            viewHolder.buttonDeleteAppointment.visibility = View.GONE
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateDataSet(newDataSet: List<Agendamento>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataSet.size)
    }
}
