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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecondScreen(viewModel: CounterAppViewModel, navController: NavController) {
    val dias = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Garante que o fundo da tela seja do tema
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row {
            OutlinedTextField(
                value = viewModel.config.value.nomeAlarme,
                onValueChange = { newText -> viewModel.updateNome(newText) },
                label = { Text("Nome do Alarme") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface // Usa onSurface do tema para o texto digitado
                )
                // Se o fundo do OutlinedTextField ainda for branco no modo noturno,
                // e você quiser que ele seja escuro como o background, pode tentar:
                /* colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ) */
            )

            Spacer(modifier = Modifier.width(10.dp))

            Switch(
                checked = viewModel.config.value.ativo,
                onCheckedChange = {viewModel.toggleAtivo()}
            )
        }

        FlowRow {
            dias.forEachIndexed { index, dia ->
                Column {
                    Checkbox(
                        checked = viewModel.config.value.diasSemana[index].toString() == "1",
                        onCheckedChange = { viewModel.updateDiaSemana(index) }
                    )
                    // AQUI: Define a cor do texto para os dias da semana
                    Text(
                        text = dia,
                        color = MaterialTheme.colorScheme.onBackground // Use onBackground ou onSurface
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
                    navController.navigate("Tela1")
                }
            ) {
                Text(text="Adicionar alarme")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    navController.navigate("Tela1")
                }
            ) {
                Text(text="Cancelar")
            }
        }
    }
}