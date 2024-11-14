package com.fisi.sisvita.ui.screens.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.repository.RegisterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel (private val registerRepository: RegisterRepository) : ViewModel() {
    private val _registerState = MutableStateFlow(false)
    val registerState: StateFlow<Boolean> = _registerState.asStateFlow()

    var showErrorDialog by mutableStateOf(false)
    var documentTypes by mutableStateOf(emptyList<String>())
    var genders by mutableStateOf(emptyList<String>())
    var departments by mutableStateOf(emptyList<String>())
    var provinces by mutableStateOf(emptyList<String>())
    var districts by mutableStateOf(emptyList<String>())

    init {
        getDocumentTypes()
        getGenders()
        getDepartments()
    }

    private fun getDocumentTypes() {
        viewModelScope.launch {
            documentTypes = registerRepository.getDocumentTypes()
        }
    }

    private fun getGenders() {
        viewModelScope.launch{
            genders = registerRepository.getGenders()
        }
    }

    private fun getDepartments() {
        viewModelScope.launch{
            departments = registerRepository.getDepartments()
        }
    }

    fun getProvinces(department: String) {
        viewModelScope.launch {
            provinces = registerRepository.getProvinces(department)
        }
    }

    fun getDistricts(department: String, province: String) {
        viewModelScope.launch {
            districts = registerRepository.getDistricts(department, province)
        }
    }

    fun register(documenttype: String, documentcharacter: String, birthdate: String,email: String,name: String,lastname: String,
                 secondlastname: String,gender: String, phone: String, department: String,province: String,district: String, username: String, password: String, role : String) {
        viewModelScope.launch {
            val success = registerRepository.register(documenttype, documentcharacter, birthdate,email,name,lastname,secondlastname,gender, phone, department,province,district, username, password, role)
            // Log.d("RegisterViewModel", "Registering with: $documenttype, $documentcharacter, $birthdate, $email, $name, $lastname, $secondlastname, $gender, $phone, $department, $province, $district, $username, $password")
            if (success) {
                _registerState.value = true
            } else {
                showErrorDialog = true
            }
        }
    }

}