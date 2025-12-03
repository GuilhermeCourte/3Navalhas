package com.example.a3navalhas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class TimeSlotAdapter(
    private var timeSlots: List<String>,
    private val onTimeSlotClick: (String) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_slot, parent, false) as MaterialButton
        return TimeSlotViewHolder(button)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val timeSlot = timeSlots[position]
        holder.bind(timeSlot, onTimeSlotClick)
    }

    override fun getItemCount(): Int = timeSlots.size

    fun updateDataSet(newTimeSlots: List<String>) {
        timeSlots = newTimeSlots
        notifyDataSetChanged()
    }

    class TimeSlotViewHolder(private val button: MaterialButton) : RecyclerView.ViewHolder(button) {
        fun bind(time: String, onTimeSlotClick: (String) -> Unit) {
            button.text = time
            button.setOnClickListener {
                onTimeSlotClick(time)
            }
        }
    }
}
