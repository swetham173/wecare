package com.example.convo
sealed class UserTypeState {
    data class Success(val type: String) : UserTypeState()
    object Loading : UserTypeState()
    object Error : UserTypeState()
}
