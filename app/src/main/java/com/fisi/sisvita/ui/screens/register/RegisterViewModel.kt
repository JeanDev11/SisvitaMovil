import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisi.sisvita.data.repository.RegisterRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RegisterViewModel(private val registerRepository: RegisterRepository) : ViewModel() {
    private val _registerState = MutableStateFlow(false)
    val registerState: StateFlow<Boolean> = _registerState.asStateFlow()

    var showErrorDialog by mutableStateOf(false)
    var documentTypes by mutableStateOf(emptyList<String>())
    var genders by mutableStateOf(emptyList<String>())
    var departments by mutableStateOf(emptyList<String>())
    var provinces by mutableStateOf(emptyList<String>())
    var districts by mutableStateOf(emptyList<String>())
    var isLoading by mutableStateOf(false)


    init {
        getDocumentTypes()
        getGenders()
        getDepartments()
    }


    private fun getDocumentTypes() {
        viewModelScope.launch {
            isLoading = true
            var retries = 0
            val maxRetries = 5
            while (documentTypes.isEmpty() && retries < maxRetries) {
                try {
                    documentTypes = registerRepository.getDocumentTypes()
                    if (documentTypes.isEmpty()) {
                        Log.e("RegisterViewModel", "Lista de tipos de documento vacía")
                        delay(1000) // Retry after 1 second
                    }
                } catch (e: Exception) {
                    Log.e("RegisterViewModel", "Error al obtener tipos de documento: ${e.message}")
                    showErrorDialog = true
                    delay(1000) // Retry after 1 second
                }
                retries++
            }
            isLoading = false
        }
    }

    private fun getGenders() {
        viewModelScope.launch {
            isLoading = true
            var retries = 0
            val maxRetries = 5
            while (genders.isEmpty() && retries < maxRetries) {
                try {
                    genders = registerRepository.getGenders()
                    if (genders.isEmpty()) {
                        Log.e("RegisterViewModel", "Lista de géneros vacía")
                        delay(1000) // Retry after 1 second
                    }
                } catch (e: Exception) {
                    Log.e("RegisterViewModel", "Error al obtener géneros: ${e.message}")
                    delay(1000) // Retry after 1 second
                }
                retries++
            }
            isLoading = false
        }
    }

    private fun getDepartments() {
        viewModelScope.launch {
            isLoading = true
            var retries = 0
            val maxRetries = 5
            while (departments.isEmpty() && retries < maxRetries) {
                try {
                    departments = registerRepository.getDepartments()
                    if (departments.isEmpty()) {
                        Log.e("RegisterViewModel", "Lista de departamentos vacía")
                        delay(1000) // Retry after 1 second
                    }
                } catch (e: Exception) {
                    Log.e("RegisterViewModel", "Error al obtener departamentos: ${e.message}")
                    delay(1000) // Retry after 1 second
                }
                retries++
            }
            isLoading = false
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


    fun register(documentType: String, documentCharacter: String, birthDate: String, email: String, firstName: String, lastName: String,
                 middleName: String, gender: String, phone: String, department: String, province: String, district: String, username: String, password: String, role: String) {
        viewModelScope.launch {
            try {
                val success = registerRepository.register(documentType, documentCharacter, birthDate, email, firstName, lastName, middleName, gender, phone, department, province, district, username, password, role)
                Log.d("RegisterViewModel", "API response: $success")
                if (success) {
                    _registerState.value = true
                } else {
                    showErrorDialog = true
                }
            } catch (e: Exception) {
                showErrorDialog = true
                Log.e("RegisterViewModel", "Registration failed", e)
            }
        }
    }
}
