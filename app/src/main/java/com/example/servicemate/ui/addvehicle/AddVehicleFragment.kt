package com.example.servicemate.ui.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.servicemate.data.AppDatabase
import com.example.servicemate.data.model.Vehicle
import com.example.servicemate.data.repository.VehicleRepository
import com.example.servicemate.databinding.FragmentAddVehicleBinding
import com.example.servicemate.util.SessionManager
import kotlinx.coroutines.launch

class AddVehicleFragment : Fragment() {

    private var _binding: FragmentAddVehicleBinding? = null
    private val binding get() = _binding!!

    private lateinit var vehicleRepository: VehicleRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        vehicleRepository = VehicleRepository(db.vehicleDao())

        binding.btnSaveVehicle.setOnClickListener { attemptSave() }
    }

    private fun attemptSave() {
        val make = binding.etMake.text.toString().trim()
        val model = binding.etModel.text.toString().trim()
        val yearText = binding.etYear.text.toString().trim()
        val licensePlate = binding.etLicensePlate.text.toString().trim()
        val odometerText = binding.etOdometer.text.toString().trim()

        binding.tilMake.error = null
        binding.tilModel.error = null
        binding.tilYear.error = null
        binding.tilLicensePlate.error = null
        binding.tilOdometer.error = null

        var hasError = false
        if (make.isEmpty()) { binding.tilMake.error = "Required"; hasError = true }
        if (model.isEmpty()) { binding.tilModel.error = "Required"; hasError = true }

        val year = yearText.toIntOrNull()
        if (year == null) { binding.tilYear.error = "Enter a valid year"; hasError = true }

        if (licensePlate.isEmpty()) { binding.tilLicensePlate.error = "Required"; hasError = true }

        val odometer = odometerText.toIntOrNull()
        if (odometer == null) { binding.tilOdometer.error = "Enter a valid number"; hasError = true }

        if (hasError) return

        val ownerId = SessionManager.getLoggedInUserId(requireContext())

        val vehicle = Vehicle(
            ownerId = ownerId,
            make = make,
            model = model,
            year = year!!,
            licensePlate = licensePlate,
            currentOdometer = odometer!!
        )

        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            vehicleRepository.addVehicle(vehicle)
            setLoading(false)
            Toast.makeText(requireContext(), "Vehicle added", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSaveVehicle.isEnabled = !loading
        binding.btnSaveVehicle.text = if (loading) "" else "Save Vehicle"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}