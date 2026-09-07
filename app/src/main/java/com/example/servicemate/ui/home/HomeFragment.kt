package com.example.servicemate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servicemate.R
import com.example.servicemate.data.AppDatabase
import com.example.servicemate.data.repository.UserRepository
import com.example.servicemate.data.repository.VehicleRepository
import com.example.servicemate.databinding.FragmentHomeBinding
import com.example.servicemate.ui.adapter.VehicleAdapter
import com.example.servicemate.util.SessionManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository
    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var vehicleAdapter: VehicleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        userRepository = UserRepository(db.userDao())
        vehicleRepository = VehicleRepository(db.vehicleDao())

        val ownerId = SessionManager.getLoggedInUserId(requireContext())
        if (ownerId == -1L) {
            // Safety net: shouldn't happen, but if session is missing, send back to login
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            return
        }

        setupRecyclerView(ownerId)
        loadGreeting(ownerId)
        observeVehicles(ownerId)

        binding.fabAddVehicle.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addVehicleFragment)
        }

        binding.tvReminders.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_remindersFragment)
        }

        binding.tvProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.tvLogout.setOnClickListener {
            SessionManager.clearSession(requireContext())
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }

    private fun setupRecyclerView(ownerId: Long) {
        vehicleAdapter = VehicleAdapter { vehicle ->
            findNavController().navigate(
                R.id.action_homeFragment_to_vehicleDetailFragment,
                bundleOf("vehicleId" to vehicle.id)
            )
        }
        binding.rvVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehicles.adapter = vehicleAdapter
    }

    private fun loadGreeting(ownerId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val user = AppDatabase.getInstance(requireContext()).userDao().getById(ownerId)
            binding.tvGreeting.text = if (user != null) {
                "Hello, ${user.fullName.substringBefore(" ")}"
            } else {
                "Hello!"
            }
        }
    }

    private fun observeVehicles(ownerId: Long) {
        vehicleRepository.getVehiclesForOwner(ownerId).observe(viewLifecycleOwner) { vehicles ->
            vehicleAdapter.submitList(vehicles)
            val isEmpty = vehicles.isEmpty()
            binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.rvVehicles.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.tvVehicleCount.text = when (vehicles.size) {
                0 -> "No vehicles yet"
                1 -> "1 vehicle in your garage"
                else -> "${vehicles.size} vehicles in your garage"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}