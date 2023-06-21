package com.isfan17.classevents.ui.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.isfan17.classevents.R
import com.isfan17.classevents.databinding.FragmentLoginBinding
import com.isfan17.classevents.ui.components.LoadingDialog
import com.isfan17.classevents.utils.Helper.toast
import com.isfan17.classevents.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loadingDialog = LoadingDialog(this)

        // Validating user data
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginFlow.collect { state ->
                    if (state != null) {
                        when(state) {
                            Resource.Loading -> loadingDialog.show()
                            is Resource.Failure -> {
                                loadingDialog.dismiss()
                                toast(state.error.toString())
                            }
                            is Resource.Success -> {
                                loadingDialog.dismiss()
                                toast(getString(R.string.msg_login_success))
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            }
                        }
                    }
                }
            }
        }

        // Login with user input
        binding.btnLogin.setOnClickListener {
            val emailEntry = binding.edtEmail.text.toString()
            val passwordEntry = binding.edtPassword.text.toString()

            when {
                emailEntry.isEmpty() -> binding.edtEmail.error = getString(R.string.msg_field_required)
                passwordEntry.isEmpty() -> binding.edtPassword.error = getString(R.string.msg_field_required)

                else -> viewModel.login(emailEntry, passwordEntry)
            }
        }

        // Move to register page
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}