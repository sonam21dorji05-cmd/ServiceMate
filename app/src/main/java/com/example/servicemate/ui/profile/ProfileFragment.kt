package com.example.servicemate.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.servicemate.R
import com.example.servicemate.data.AppDatabase
import com.example.servicemate.data.repository.UserRepository
import com.example.servicemate.databinding.FragmentProfileBinding
import com.example.servicemate.util.SessionManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        userRepository = UserRepository(db.userDao())

        val ownerId = SessionManager.getLoggedInUserId(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val user = db.userDao().getById(ownerId)
            if (user != null) {
                binding.tvFullName.text = user.fullName
                binding.tvEmail.text = user.email
            }
        }

        binding.tvManageVehicles.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.tvViewReminders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_remindersFragment)
        }

        binding.btnLogout.setOnClickListener {
            SessionManager.clearSession(requireContext())
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}