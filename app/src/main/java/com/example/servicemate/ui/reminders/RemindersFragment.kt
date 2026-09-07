package com.example.servicemate.ui.reminders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servicemate.data.AppDatabase
import com.example.servicemate.data.model.Vehicle
import com.example.servicemate.data.repository.ReminderRepository
import com.example.servicemate.databinding.FragmentRemindersBinding
import com.example.servicemate.ui.adapter.ReminderAdapter
import com.example.servicemate.ui.adapter.ReminderRow
import com.example.servicemate.util.SessionManager
import kotlinx.coroutines.launch

class RemindersFragment : Fragment() {

    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!

    private lateinit var reminderRepository: ReminderRepository
    private lateinit var reminderAdapter: ReminderAdapter
    private var vehicleMap: Map<Long, Vehicle> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        reminderRepository = ReminderRepository(db.reminderDao())
        val ownerId = SessionManager.getLoggedInUserId(requireContext())

        reminderAdapter = ReminderAdapter()
        binding.rvReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReminders.adapter = reminderAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            val allVehicles = db.vehicleDao().getAllVehicles()
            vehicleMap = allVehicles.filter { it.ownerId == ownerId }.associateBy { it.id }

            reminderRepository.getActiveRemindersForOwner(ownerId).observe(viewLifecycleOwner) { reminders ->
                val rows = reminders.mapNotNull { reminder ->
                    vehicleMap[reminder.vehicleId]?.let { vehicle -> ReminderRow(reminder, vehicle) }
                }.sortedWith(compareByDescending { it.reminder.dueDateMillis == null })

                reminderAdapter.submitList(rows)
                val isEmpty = rows.isEmpty()
                binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                binding.rvReminders.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}