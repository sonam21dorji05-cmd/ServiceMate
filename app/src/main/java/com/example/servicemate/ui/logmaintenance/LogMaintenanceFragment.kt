package com.example.servicemate.ui.logmaintenance

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.servicemate.data.AppDatabase
import com.example.servicemate.data.model.MaintenanceRecord
import com.example.servicemate.data.repository.MaintenanceRepository
import com.example.servicemate.data.repository.VehicleRepository
import com.example.servicemate.databinding.FragmentLogMaintenanceBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LogMaintenanceFragment : Fragment() {

    private var _binding: FragmentLogMaintenanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var maintenanceRepository: MaintenanceRepository
    private lateinit var vehicleRepository: VehicleRepository

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private var selectedDateMillis: Long = System.currentTimeMillis()

    private val serviceTypes = listOf(
        "Oil Change", "Tire Rotation", "Brake Service",
        "Battery Replacement", "Air Filter", "General Inspection", "Other"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogMaintenanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        maintenanceRepository = MaintenanceRepository(db.maintenanceDao())
        vehicleRepository = VehicleRepository(db.vehicleDao())

        val vehicleId = requireArguments().getLong("vehicleId")

        setupServiceTypeDropdown()
        setupDatePicker()
        binding.etDate.setText(dateFormat.format(calendar.time))

        binding.btnSaveRecord.setOnClickListener { attemptSave(vehicleId) }
    }

    private fun setupServiceTypeDropdown() {
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, serviceTypes)
        binding.actServiceType.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDateMillis = calendar.timeInMillis
                    binding.etDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun attemptSave(vehicleId: Long) {
        val serviceType = binding.actServiceType.text.toString().trim()
        val odometerText = binding.etOdometer.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        binding.tilServiceType.error = null
        binding.tilOdometer.error = null

        var hasError = false
        if (serviceType.isEmpty()) {
            binding.tilServiceType.error = "Select a service type"; hasError = true
        }
        val odometer = odometerText.toIntOrNull()
        if (odometer == null) {
            binding.tilOdometer.error = "Enter a valid number"; hasError = true
        }
        if (hasError) return

        val record = MaintenanceRecord(
            vehicleId = vehicleId,
            serviceType = serviceType,
            serviceDateMillis = selectedDateMillis,
            odometerReading = odometer!!,
            notes = notes
        )

        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            maintenanceRepository.addRecord(record)

            // Keep the vehicle's current odometer in sync with the latest service reading
            val vehicle = vehicleRepository.getVehicleById(vehicleId)
            if (vehicle != null && odometer > vehicle.currentOdometer) {
                vehicleRepository.updateVehicle(vehicle.copy(currentOdometer = odometer))
            }

            setLoading(false)
            Toast.makeText(requireContext(), "Maintenance record saved", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSaveRecord.isEnabled = !loading
        binding.btnSaveRecord.text = if (loading) "" else "Save Record"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}