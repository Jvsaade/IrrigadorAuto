package com.example.kotlinviewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecondScreen(viewModel: CounterAppViewModel, navController: NavController, alarmId: String?) { // Aceita alarmId como String
    val dias = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")

    val currentAlarmId = rememberUpdatedState(alarmId)

    LaunchedEffect(currentAlarmId.value) {
        currentAlarmId.value?.let { idString ->
            if (idString != "new") { // Verifica se não é "new"
                idString.toIntOrNull()?.let { id -> // Tenta converter para Int
                    viewModel.loadAlarmForEditing(id)
                } ?: run {
                    // Lidar com um ID inválido que não seja "new"
                    viewModel.resetConfig()
                }
            } else {
                viewModel.resetConfig()
            }
        } ?: run {
            viewModel.resetConfig()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row {
            OutlinedTextField(
                value = viewModel.config.value.nomeAlarme,
                onValueChange = { newText -> viewModel.updateNome(newText) },
                label = { Text("Nome do Alarme") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Switch(
                checked = viewModel.config.value.ativo,
                onCheckedChange = { viewModel.toggleAtivo() }
            )
        }

        FlowRow {
            dias.forEachIndexed { index, dia ->
                Column {
                    Checkbox(
                        checked = viewModel.config.value.diasSemana[index].toString() == "1",
                        onCheckedChange = { viewModel.updateDiaSemana(index) }
                    )
                    Text(
                        text = dia,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .height(30.dp)
        )

        FlowRow {
            Button(
                onClick = {
                    viewModel.salvarAlarme()
                    navController.navigate("Tela1") {
                        popUpTo("Tela1") {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text(text = "Salvar alarme")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    navController.navigate("Tela1") {
                        popUpTo("Tela1") {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text(text = "Cancelar")
            }
        }
    }
}