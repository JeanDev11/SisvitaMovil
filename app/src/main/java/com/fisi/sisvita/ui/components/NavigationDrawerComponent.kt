package com.fisi.sisvita.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fisi.sisvita.R
import com.fisi.sisvita.ui.theme.SisvitaTheme

@Composable
fun NavigationDrawerContent(
    userName: String,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
){
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(240.dp),
                drawerContainerColor = MaterialTheme.colorScheme.primary
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Usuario
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "User profile",
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = userName, style = MaterialTheme.typography.titleMedium)
                }

                HorizontalDivider()

                // Opciones del menú
                NavigationDrawerItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = null) },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = { onItemClick("Inicio") }
                )
                NavigationDrawerItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_test), contentDescription = null) },
                    label = { Text("Test") },
                    selected = false,
                    onClick = { onItemClick("Test") }
                )
                NavigationDrawerItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_help), contentDescription = null) },
                    label = { Text("Necesito ayuda") },
                    selected = false,
                    onClick = { onItemClick("Necesito ayuda") }
                )
                NavigationDrawerItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_history), contentDescription = null) },
                    label = { Text("Historial") },
                    selected = false,
                    onClick = { onItemClick("Historial") }
                )
                NavigationDrawerItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_account), contentDescription = null) },
                    label = { Text("Cuenta") },
                    selected = false,
                    onClick = { onItemClick("Cuenta") }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_logout), contentDescription = null) },
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = onLogout
                )
            }
        },
        content = {
            // Aquí va el contenido principal
        }
    )
}


@Preview
@Composable
fun PreviewNavigationDrawer() {
    SisvitaTheme(darkTheme = false) {
//        NavigationDrawerContent(
//            userName = "Linna Jimenez",
//            onItemClick = {},
//            onLogout = {}
//        )
    }
}