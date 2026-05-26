package com.example.rockstar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rockstar.model.UserModel
import com.example.rockstar.repo.UserRepo

class UserViewModel(private val repo: UserRepo) : ViewModel() {

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData

    private val _allUsers = MutableLiveData<List<UserModel?>>()
    val allUsers: LiveData<List<UserModel?>> = _allUsers

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        _loading.value = true
        repo.login(email, password) { success, msg ->
            _loading.value = false
            _message.value = msg ?: ""
            onResult(success)
        }
    }

    fun register(email: String, password: String, model: UserModel, onResult: (Boolean) -> Unit) {
        _loading.value = true
        repo.register(email, password) { success, msg, uid ->
            if (success) {
                val userWithId = model.copy(id = uid)
                repo.addUser(uid, userWithId) { addSuccess, addMsg ->
                    _loading.value = false
                    _message.value = addMsg
                    onResult(addSuccess)
                }
            } else {
                _loading.value = false
                _message.value = msg
                onResult(false)
            }
        }
    }

    fun getUserById() {
        _loading.value = true
        repo.getUserById { success, msg, users ->
            _loading.value = false
            if (success && users.isNotEmpty()) {
                _userData.value = users[0]
            } else {
                _message.value = msg
            }
        }
    }

    fun getAllUsers() {
        _loading.value = true
        repo.getAllUsers { success, msg, users ->
            _loading.value = false
            if (success) {
                _allUsers.value = users
            } else {
                _message.value = msg
            }
        }
    }

    fun logout(onResult: (Boolean) -> Unit) {
        repo.logout { success ->
            onResult(success)
        }
    }
}
