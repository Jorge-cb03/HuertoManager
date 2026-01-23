package com.example.proyecto.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    // Comprobar si ya entramos logueados al abrir la app
    fun isUserLoggedIn(): Boolean = repository.currentUser != null

    fun loginAnonymously(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null

            val result = repository.loginAnonymously()

            _isLoading.value = false

            if (result.isSuccess) {
                onSuccess()
            } else {
                _loginError.value = "Error: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}