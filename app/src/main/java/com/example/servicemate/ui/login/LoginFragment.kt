package com.example.servicemate.ui.login

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
import com.example.servicemate.databinding.FragmentLoginBinding
import com.example.servicemate.util.SessionManager
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        userRepository = UserRepository(db.userDao())

        // If already logged in, skip straight to home
        if (SessionManager.isLoggedIn(requireContext())) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            return
        }

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvSignUpLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        binding.tilEmail.error = null
        binding.tilPassword.error = null

        var hasError = false
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            hasError = true
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            hasError = true
        }
        if (hasError) return

        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            val result = userRepository.login(email, password)
            setLoading(false)
            result.onSuccess { user ->
                SessionManager.saveSession(requireContext(), user.id)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading
        binding.btnLogin.text = if (loading) "" else "Log In"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}