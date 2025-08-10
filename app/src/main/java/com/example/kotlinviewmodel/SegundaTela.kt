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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(viewModel: CounterAppViewModel, navController: NavController, alarmId: String?) {
    val dias = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
    val currentAlarmId = rememberUpdatedState(alarmId)
    val timePickerState = rememberTimePickerState(
        initialHour = viewModel.config.value.horaAlarme,
        initialMinute = viewModel.config.value.minutoAlarme,
        is24Hour = true,
    )

    // Adiciona um estado para armazenar o nome antigo do alarme
    val nomeAntigo = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val statusMessage by viewModel.statusMessage

    // Efeito para carregar o alarme e armazenar o nome antigo
    LaunchedEffect(currentAlarmId.value) {
        currentAlarmId.value?.let { idString ->
            if (idString != "new") {
                idString.toIntOrNull()?.let { id ->
                    viewModel.loadAlarmForEditing(id)
                    // Armazena o nome antigo após carregar o alarme
                    nomeAntigo.value = viewModel.config.value.nomeAlarme
                } ?: run {
                    viewModel.resetConfig()
                }
            } else {
                viewModel.resetConfig()
            }
        } ?: run {
            viewModel.resetConfig()
        }
    }

    // Efeito para mostrar o SnackBar
    LaunchedEffect(statusMessage) {
        statusMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
            viewModel.onStatusMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

            Spacer(modifier = Modifier.height(20.dp))

            TimeInput(
                state = timePickerState
            )

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
                        viewModel.updateHoraMinuto(timePickerState.hour, timePickerState.minute)
                        if (alarmId != "new") {
                            // Se for uma edição, chama a nova função com o nome antigo
                            viewModel.editarAlarme(nomeAntigo.value) { sucesso ->
                                if (sucesso) {
                                    navController.navigate("Tela1") {
                                        popUpTo("Tela1") { inclusive = true }
                                    }
                                }
                                // Se falhar, a mensagem de erro é exibida, mas a navegação não acontece
                            }
                        } else {
                            // Se for um novo alarme, a lógica original é mantida
                            viewModel.salvarAlarme()
                            navController.navigate("Tela1") {
                                popUpTo("Tela1") { inclusive = true }
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
}