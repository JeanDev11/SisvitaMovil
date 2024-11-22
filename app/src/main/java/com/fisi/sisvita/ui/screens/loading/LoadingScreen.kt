package com.fisi.sisvita.ui.screens.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fisi.sisvita.R
import com.fisi.sisvita.ui.components.LottieAnimationComponent
import com.fisi.sisvita.ui.theme.SisvitaTheme

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LottieAnimationComponent(
                    resId = R.raw.loading,
                    modifier = Modifier.height(120.dp)
                )
                Text(
                    text = "Se esta analizando los resultados.\n Por favor, espere...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                LottieAnimationComponent(
                    resId = R.raw.mywave,
                    scaleY = -1f
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.2f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyzingViewPreview() {
    SisvitaTheme(darkTheme = false) {
        LoadingScreen()
    }
}