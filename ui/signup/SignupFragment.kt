package com.example.servicemate.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.servicemate.R
import com.example.servicemate.data.AppDatabase
import com.example.servicemate.data.repository.UserRepository
import com.example.servicemate.databinding.FragmentSignupBinding
import com.example.servicemate.util.SessionManager
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        userRepository = UserRepository(db.userDao())

        binding.btnCreateAccount.setOnClickListener { attemptSignup() }
        binding.tvLoginLink.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun attemptSignup() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        var hasError = false
        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Name is required"; hasError = true
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"; hasError = true
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"; hasError = true
        }
        if (confirmPassword != password) {
            binding.tilConfirmPassword.error = "Passwords do not match"; hasError = true
        }
        if (hasError) return

        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            val result = userRepository.register(fullName, email, password)
            setLoading(false)
            result.onSuccess { user ->
                SessionManager.saveSession(requireContext(), user.id)
                findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnCreateAccount.isEnabled = !loading
        binding.btnCreateAccount.text = if (loading) "" else "Create Account"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}