package com.example.servicemate.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.servicemate.data.model.Vehicle
import com.example.servicemate.databinding.ItemVehicleBinding

class VehicleAdapter(
    private val onVehicleClick: (Vehicle) -> Unit
) : ListAdapter<Vehicle, VehicleAdapter.VehicleViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val binding = ItemVehicleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VehicleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VehicleViewHolder(private val binding: ItemVehicleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vehicle: Vehicle) {
            binding.tvVehicleName.text = "${vehicle.make} ${vehicle.model} ${vehicle.year}"
            binding.tvLicensePlate.text = vehicle.licensePlate
            binding.tvOdometer.text = "${vehicle.currentOdometer} miles"
            binding.root.setOnClickListener { onVehicleClick(vehicle) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Vehicle>() {
            override fun areItemsTheSame(oldItem: Vehicle, newItem: Vehicle) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Vehicle, newItem: Vehicle) =
                oldItem == newItem
        }
    }
}