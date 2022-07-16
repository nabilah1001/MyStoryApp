package com.dicoding.picodiploma.mystoryapp.view.authentification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.mystoryapp.data.User
import kotlinx.coroutines.launch

class SharedViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUser() : LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}