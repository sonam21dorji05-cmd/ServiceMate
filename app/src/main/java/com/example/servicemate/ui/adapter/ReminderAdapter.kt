package com.example.servicemate.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.servicemate.data.model.Reminder
import com.example.servicemate.data.model.Vehicle
import com.example.servicemate.databinding.ItemReminderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReminderRow(val reminder: Reminder, val vehicle: Vehicle)

class ReminderAdapter : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private var rows: List<ReminderRow> = emptyList()
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    fun submitList(newRows: List<ReminderRow>) {
        rows = newRows
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(rows[position])
    }

    override fun getItemCount() = rows.size

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(row: ReminderRow) {
            val reminder = row.reminder
            val vehicle = row.vehicle

            binding.tvServiceType.text = reminder.serviceType
            binding.tvVehicleName.text = "${vehicle.make} ${vehicle.model} ${vehicle.year}"

            val isOverdue = isOverdue(reminder, vehicle)
            binding.tvStatus.text = if (isOverdue) "OVERDUE" else "UPCOMING"
            binding.tvStatus.setBackgroundColor(
                Color.parseColor(if (isOverdue) "#D32F2F" else "#2E7D32")
            )

            val parts = mutableListOf<String>()
            reminder.dueOdometer?.let { parts.add("at $it miles") }
            reminder.dueDateMillis?.let { parts.add("by ${dateFormat.format(Date(it))}") }
            binding.tvDueInfo.text = "Due " + parts.joinToString(" / ")
        }

        private fun isOverdue(reminder: Reminder, vehicle: Vehicle): Boolean {
            val odometerOverdue = reminder.dueOdometer?.let { vehicle.currentOdometer >= it } ?: false
            val dateOverdue = reminder.dueDateMillis?.let { System.currentTimeMillis() >= it } ?: false
            return odometerOverdue || dateOverdue
        }
    }
}