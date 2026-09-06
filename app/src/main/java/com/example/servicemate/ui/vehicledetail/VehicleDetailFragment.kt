package com.example.servicemate.ui.vehicledetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servicemate.data.AppDatabase
import com.example.servicemate.data.repository.MaintenanceRepository
import com.example.servicemate.data.repository.VehicleRepository
import com.example.servicemate.databinding.FragmentVehicleDetailBinding
import com.example.servicemate.ui.adapter.MaintenanceAdapter
import kotlinx.coroutines.launch

class VehicleDetailFragment : Fragment() {

    private var _binding: FragmentVehicleDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var maintenanceRepository: MaintenanceRepository
    private lateinit var maintenanceAdapter: MaintenanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehicleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        vehicleRepository = VehicleRepository(db.vehicleDao())
        maintenanceRepository = MaintenanceRepository(db.maintenanceDao())

        val vehicleId = requireArguments().getLong("vehicleId")

        setupRecyclerView()
        loadVehicle(vehicleId)
        observeHistory(vehicleId)

        binding.btnLogMaintenance.setOnClickListener {
            // Wired up fully in Phase 5 (Log Maintenance screen)
            Toast.makeText(requireContext(), "Coming in Phase 5", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        maintenanceAdapter = MaintenanceAdapter()
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = maintenanceAdapter
    }

    private fun loadVehicle(vehicleId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val vehicle = vehicleRepository.getVehicleById(vehicleId)
            if (vehicle != null) {
                binding.tvVehicleName.text = "${vehicle.make} ${vehicle.model} ${vehicle.year}"
                binding.tvLicensePlate.text = vehicle.licensePlate
                binding.tvOdometer.text = "${vehicle.currentOdometer} miles"
            }
        }
    }

    private fun observeHistory(vehicleId: Long) {
        maintenanceRepository.getRecordsForVehicle(vehicleId).observe(viewLifecycleOwner) { records ->
            maintenanceAdapter.submitList(records)
            val isEmpty = records.isEmpty()
            binding.tvNoHistory.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.rvHistory.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}