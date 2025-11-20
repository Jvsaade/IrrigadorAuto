package com.example.kotlinviewmodel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.kotlinviewmodel.baseDados.Configuration

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FirstScreen(viewModel: CounterAppViewModel, navController: NavController) {

    val allAlarms by viewModel.allAlarms.collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    val snackbarHostState = remember { SnackbarHostState() }
    val statusMessage by viewModel.statusMessage

    LaunchedEffect(Unit) {
        viewModel.verificaAlarme() // Chamada para a nova função no ViewModel
    }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.onStatusMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    navController.navigate("Tela2/new")
                }) {
                    Text(text = "Adicionar alarme")
                }
                Button(onClick = { viewModel.excluirTodosAlarmes() }) {
                    Text(text = "Excluir todos alarmes")
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        viewModel.atualizarTimers()
                    }
                ) {
                    Text(text = "Atualizar microcontrolador")
                }

                Button(
                    onClick = {
                        viewModel.verificarBateria()
                    }
                ) {
                    Text(text = "Verificar bateria")
                }

            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            allAlarms.forEach { alarm ->
                AlarmCard(
                    viewModel = viewModel,
                    alarm = alarm,
                    onDeleteAlarm = { viewModel.excluirAlarme(alarm) },
                    onEditAlarm = { id -> navController.navigate("Tela2/$id") }
                )
            }
        }
    }
}

fun formatDays(daysString: String): String {
    val days = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
    return daysString.mapIndexed { index, c ->
        if (c == '1') days[index] else null
    }.filterNotNull().joinToString(", ")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmCard(
    viewModel: CounterAppViewModel,
    alarm: Configuration,
    onDeleteAlarm: () -> Unit,
    onEditAlarm: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header com nome do alarme e botão de exclusão
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alarm.nomeAlarme,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (viewModel.alarmeSalvo(alarm.id)) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (viewModel.alarmeSalvo(alarm.id)) "Alarme salvo" else "Alarme não salvo",
                        tint = if (viewModel.alarmeSalvo(alarm.id)) Color.Green else Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    // Botão de exclusão (mantido na parte superior)
                    Button(onClick = onDeleteAlarm) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir alarme",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linha de status e botão de configuração
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (alarm.ativo) "Ativo" else "Inativo",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (alarm.ativo)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Switch(
                        checked = alarm.ativo,
                        onCheckedChange = {viewModel.toggleAtivo(alarm.id)}, // Isso toggles o estado do alarme atualmente no ViewModel.
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
            // Mover o botão de configuração para baixo do switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // Alinha o botão à direita
                verticalAlignment = Alignment.CenterVertically
            ) {
               Text(
                   text = alarm.horaAlarme.toString().padStart(2, '0') + ":" + alarm.minutoAlarme.toString().padStart(2, '0'),
                   style = MaterialTheme.typography.titleLarge.copy(
                       fontWeight = FontWeight.Bold,
                       color = MaterialTheme.colorScheme.primary
                   ),
                   modifier = Modifier.weight(1f)
               )

                Text(
                    text = "${alarm.duracaoAlarme} min",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                )

                Button(onClick = { onEditAlarm(alarm.id) }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurar alarme",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linha de dias
            Column {
                Text(
                    text = "Dias ativos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val days = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
                    alarm.diasSemana.forEachIndexed { index, c ->
                        if (c == '1') {
                            Chip(
                                label = days[index],
                                selected = true,
                                onSelected = { /* não faz nada, apenas visual */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente Chip auxiliar
@Composable
fun Chip(
    label: String,
    selected: Boolean,
    onSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}