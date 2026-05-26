package ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rockstar.model.UserModel
import com.example.rockstar.repo.UserRepo

class UserViewModel(val repo : UserRepo) : ViewModel() {
//    class UserViewModel(val repo: UserRepo) : ViewModel() {

        fun login(
            email: String, password: String,
            callback: (Boolean, String?) -> Unit
        ) {
            repo.login(email, password, callback)
        }


        fun forgetPassword(
            email: String,
            callback: (Boolean, String?) -> Unit
        ) {
            repo.forgetPassword(email, callback)
        }

        private val _loading = MutableLiveData<Boolean>()
        val loading: MutableLiveData<Boolean> get() = _loading

        private val _users = MutableLiveData<UserModel?>()
        val users: MutableLiveData<UserModel?> get() = _users


    fun getAllUsers() {
        _loading.value = true
        repo.getAllUsers { success, message, data ->
            if (success) {
                _loading.value = false
                _allUsers.value = data
            } else {
                _loading.value = false
                _allUsers.value = emptyList()
            }

        }
    }

        private val _allUsers = MutableLiveData<List<UserModel?>>()
        val allUsers: MutableLiveData<List<UserModel?>> get() = _allUsers

        fun getAllUser() {
            _loading.value = true
            repo.getAllUsers { success, message, data ->
                if (success) {
                    _loading.value = false
                    _allUsers.value = data
                } else {
                    _loading.value = false
                    _allUsers.value = emptyList()
                }

            }
        }


        fun logout(callback: (Boolean, String) -> Unit) {
            repo.logout(callback)
        }


        //authentication
        fun register(
            email: String, password: String,
            callback: (Boolean, String, String) -> Unit
        ) {
            repo.register(email, password, callback)
        }


        fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {
            repo.addUser(id, model, callback)
        }

        fun editProfile(id: String, model: UserModel, callback: (Boolean, String?) -> Unit) {
            repo.editProfile(id, model, callback)
        }

        fun deleteUser(id: String, callback: (Boolean, String) -> Unit) {
            repo.deleteUser(id, callback)
        }
    }
