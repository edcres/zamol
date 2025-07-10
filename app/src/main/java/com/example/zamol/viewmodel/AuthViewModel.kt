package com.example.zamol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zamol.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            // Result.fold() is used to map success/failure to a sealed AuthState
            _authState.value = result.fold(
                onSuccess = { AuthState.Success },
                onFailure = { AuthState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun signup(email: String, password: String, displayName: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signup(email, password, displayName)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success },
                onFailure = { AuthState.Error(it.message ?: "Signup failed") }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }

    // To clear error or success after showing a message
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
