package com.fisi.sisvita.ui.screens.register

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fisi.sisvita.data.repository.RegisterRepository
import com.fisi.sisvita.ui.screens.register.ErrorDialog
import com.fisi.sisvita.ui.theme.SisvitaTheme
import com.fisi.sisvita.ui.screens.register.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var documentType by remember { mutableStateOf("") }
    var documentCharacter by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isDepartmentExpanded by remember { mutableStateOf(false) }
    var isProvinceExpanded by remember { mutableStateOf(false) }
    var isDistrictExpanded by remember { mutableStateOf(false) }
    var isGenderExpanded by remember { mutableStateOf(false) }
    var isDocumentTypeExpanded by remember { mutableStateOf(false) }

    val documentTypes = viewModel.documentTypes
    val genders = viewModel.genders
    val departments = viewModel.departments
    val provinces = viewModel.provinces
    val districts = viewModel.districts

    if (viewModel.showErrorDialog) {
        ErrorDialog { viewModel.showErrorDialog = false }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Campo de correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Campo de nombre de usuario
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Campo de nombre
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Campo de apellido paterno y materno
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("A. Paterno") },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            )
            OutlinedTextField(
                value = middleName,
                onValueChange = { middleName = it },
                label = { Text("A. Materno") },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            )
        }

        // Menú desplegable para Tipo de Documento y Campo para Número de Documento
        Row(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = documentType.isNotEmpty(),
                onExpandedChange = { isDocumentTypeExpanded = !isDocumentTypeExpanded },
                modifier = Modifier
                    .weight(0.6f)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                OutlinedTextField(
                    value = documentType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo") },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isDocumentTypeExpanded,
                    onDismissRequest = { isDocumentTypeExpanded = false }
                ) {
                    documentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                documentType = type
                                isDocumentTypeExpanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = documentCharacter,
                onValueChange = { if (it.all { char -> char.isDigit() }) documentCharacter = it },
                label = { Text("Nº de documento") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1.4f)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            )
        }

        // Menú desplegable para Género y Campo para Teléfono
        Row(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = gender.isNotEmpty(),
                onExpandedChange = { isGenderExpanded = !isGenderExpanded },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género") },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isGenderExpanded,
                    onDismissRequest = { isGenderExpanded = false }
                ) {
                    genders.forEach { gen ->
                        DropdownMenuItem(
                            text = { Text(gen) },
                            onClick = {
                                gender = gen
                                isGenderExpanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.all { char -> char.isDigit() }) phone = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .weight(1.5f)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            )
        }

        // Menú desplegable para Departamento
        ExposedDropdownMenuBox(
            expanded = department.isNotEmpty(),
            onExpandedChange = { isDepartmentExpanded = !isDepartmentExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = department,
                onValueChange = {},
                readOnly = true,
                label = { Text("Departamento") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isDepartmentExpanded,
                onDismissRequest = { isDepartmentExpanded = false }
            ) {
                departments.forEach { dept ->
                    DropdownMenuItem(
                        text = { Text(dept) },
                        onClick = {
                            department = dept
                            isDepartmentExpanded = false
                            viewModel.getProvinces(department)
                        }
                    )
                }
            }
        }

        // Campo de provincia y distrito
        Row(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = province.isNotEmpty(),
                onExpandedChange = { isProvinceExpanded = !isProvinceExpanded },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                OutlinedTextField(
                    value = province,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Provincia") },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isProvinceExpanded,
                    onDismissRequest = { isProvinceExpanded = false }
                ) {
                    provinces.forEach { prov ->
                        DropdownMenuItem(
                            text = { Text(prov) },
                            onClick = {
                                province = prov
                                isProvinceExpanded = false
                                viewModel.getDistricts(department, province)
                            }
                        )
                    }
                }
            }
            ExposedDropdownMenuBox(
                expanded = district.isNotEmpty(),
                onExpandedChange = { isDistrictExpanded = !isDistrictExpanded },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                OutlinedTextField(
                    value = district,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Distrito") },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isDistrictExpanded,
                    onDismissRequest = { isDistrictExpanded = false }
                ) {
                    districts.forEach { dist ->
                        DropdownMenuItem(
                            text = { Text(dist) },
                            onClick = {
                                district = dist
                                isDistrictExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Campo de fecha de nacimiento
        OutlinedTextField(
            value = birthDate,
            onValueChange = {
                if (it.length <= 10 && it.matches(Regex("^\\d{0,4}-?\\d{0,2}-?\\d{0,2}\$"))) {
                    birthDate = formatBirthDate(it)
                }
            },
            label = { Text("Fecha de nacimiento (YYYY-MM-DD)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Button(
            onClick = {
                viewModel.register(documentType, documentCharacter, birthDate, email, firstName, lastName, middleName,
                    gender, phone, department, province, district, username, password, "2")
                // Log.d("RegisterScreen", "Registering with: $documentType, $documentCharacter, $birthDate, $email, $firstName, $lastName, $middleName, $gender, $phone, $department, $province, $district, $username, $password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Sign Up")
        }
    }
}

fun formatBirthDate(input: String): String {
    val digits = input.filter { it.isDigit() }
    return when {
        digits.length in 5..6 -> "${digits.substring(0, 4)}-${digits.substring(4)}"
        digits.length >= 7 -> "${digits.substring(0, 4)}-${digits.substring(4, 6)}-${digits.substring(6)}"
        digits.length >= 4 -> "${digits.substring(0, 4)}-"
        else -> digits
    }
}

@Composable
fun ErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text("Error al registrar. Por favor, intente nuevamente.") },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    SisvitaTheme {
        RegisterScreen(viewModel = RegisterViewModel(RegisterRepository()))
    }
}