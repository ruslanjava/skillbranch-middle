package ru.skillbranch.sbdelivery.screens.demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.screens.demo.DemoViewModel
import ru.skillbranch.sbdelivery.screens.demo.logic.DemoFeature

@Composable
fun DemoScreen(vm: DemoViewModel) {
    val state by vm.feature.state.collectAsState()
    ELMDemo(value = state.count, isLoading = state.isLoading, vm::accept)
}

@Composable
fun ELMDemo(value: Int, isLoading: Boolean, accept: (DemoFeature.Msg) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!isLoading) {
            Text(
                text = "$value",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CircularProgressIndicator()
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { accept(DemoFeature.Msg.NextRandom) }) {
                Text(text = "Next")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { accept(DemoFeature.Msg.Clear) }) {
                Text(text = "Clear")
            }
        }
    }
}

@Preview
@Composable
fun ElmPreview() {
    ELMDemo(value = 0, false, { })
}