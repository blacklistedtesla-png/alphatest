package com.alfabank.qapp.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alfabank.qapp.R
import com.alfabank.qapp.data.RemoteDataSourceProtocol
import com.alfabank.qapp.data.model.LoginInputData
import com.alfabank.qapp.databinding.FragmentLoginBinding
import com.alfabank.qapp.domain.LoginGateway
import com.alfabank.qapp.domain.LoginUseCase
import kotlinx.coroutines.flow.collect

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by lazy {
        LoginViewModel(LoginUseCase(LoginGateway(RemoteDataSourceProtocol())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etUsername.doAfterTextChanged {
            binding.tvError.text = ""
        }

        binding.etPassword.doAfterTextChanged {
            binding.tvError.text = ""
        }

        lifecycleScope.launchWhenCreated {
            viewModel.viewState.collect {
                when (it) {
                    is LoginViewState.Login -> {
                        binding.loader.isVisible = false
                        if (it.result == LoginUseCase.LoginResult.Success) {
                            findNavController().navigate(R.id.action_login_to_main)
                        } else {
                            binding.tvError.setText(R.string.incorrect_credentials)
                        }
                    }
                    is LoginViewState.Loader -> {
                        binding.loader.isVisible = it.showed
                    }
                    null -> Unit
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.onUiEvent(
                LoginUiEvent.Login(
                    LoginInputData(
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString()
                    )
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}