package com.example.servicemate.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.servicemate.data.model.MaintenanceRecord
import com.example.servicemate.databinding.ItemMaintenanceBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MaintenanceAdapter :
    ListAdapter<MaintenanceRecord, MaintenanceAdapter.MaintenanceViewHolder>(DIFF_CALLBACK) {

    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val binding = ItemMaintenanceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MaintenanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MaintenanceViewHolder(private val binding: ItemMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: MaintenanceRecord) {
            binding.tvServiceType.text = record.serviceType
            binding.tvOdometer.text = "${record.odometerReading} mi"
            binding.tvDate.text = dateFormat.format(Date(record.serviceDateMillis))
            binding.tvNotes.text = record.notes
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MaintenanceRecord>() {
            override fun areItemsTheSame(oldItem: MaintenanceRecord, newItem: MaintenanceRecord) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MaintenanceRecord, newItem: MaintenanceRecord) =
                oldItem == newItem
        }
    }
}