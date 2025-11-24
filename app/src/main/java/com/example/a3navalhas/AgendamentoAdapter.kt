package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AgendamentoAdapter(
    private val dataSet: MutableList<Agendamento>
) : RecyclerView.Adapter<AgendamentoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewClientName: TextView = view.findViewById(R.id.textViewClientName)
        val textViewAppointmentDateTime: TextView = view.findViewById(R.id.textViewAppointmentDateTime)
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
    }

    override fun getItemCount() = dataSet.size

    fun updateDataSet(newDataSet: List<Agendamento>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }
}
