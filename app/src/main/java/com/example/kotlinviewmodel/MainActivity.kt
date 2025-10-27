package com.example.kotlinviewmodel

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotlinviewmodel.baseDados.BaseDados
import com.example.kotlinviewmodel.baseDados.Repository
import com.example.kotlinviewmodel.network.IntApi
import com.example.kotlinviewmodel.ui.theme.KotlinViewModelTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinViewModelTheme {
                val applicationContext = LocalContext.current.applicationContext as Application
                val database = BaseDados.getDatabase(applicationContext)
                val repository = Repository(database.ConfigurationDao)

                val viewModel: CounterAppViewModel = viewModel(
                    factory = CounterAppViewModelFactory(repository)
                )
                verificaAlarme(viewModel)
                TelaPrincipal(viewModel)
            }
        }
    }
}

@Composable
fun TelaPrincipal(viewModel: CounterAppViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Tela1") {
        composable("Tela1") {
            FirstScreen(viewModel = viewModel, navController = navController)
        }
        composable(
            "Tela2/{alarmId}", // Define a rota com um argumento
            arguments = listOf(navArgument("alarmId") { type = NavType.StringType; defaultValue = "new" }) // Altera para StringType e default "new"
        ) { backStackEntry ->
            val alarmId = backStackEntry.arguments?.getString("alarmId")
            SecondScreen(viewModel = viewModel, navController = navController, alarmId = alarmId)
        }
    }
}

class CounterAppViewModelFactory(
    private val repository: Repository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterAppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CounterAppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

fun verificaAlarme(viewModel: CounterAppViewModel){
    // Usa o escopo do ViewModel para lançar uma coroutine
    viewModel.viewModelScope.launch {
        Log.d("VerificaAlarmes", "Iniciando verificação de alarmes no dispositivo...")
        try {
            // Pega a lista atual de alarmes do banco de dados
            val alarmesNoBanco = viewModel.allAlarms.first()

            if (alarmesNoBanco.isEmpty()) {
                Log.d("VerificaAlarmes", "Nenhum alarme local para verificar.")
                return@launch
            }

            // Itera por cada alarme e consulta o dispositivo
            alarmesNoBanco.forEach { alarme ->
                try {
                    val response = IntApi.intService.consultAlarm(alarme.nomeAlarme)

                    if (response.isSuccessful && response.body() != null) {
                        val existeNoDispositivo = response.body()!!
                        if (existeNoDispositivo == "True") {
                            // Se existe, marca como "enviado" (sincronizado)
                            viewModel.marcarAlarmeComoEnviado(alarme.id)
                            Log.i("VerificaAlarmes", "Alarme '${alarme.nomeAlarme}' (ID: ${alarme.id}) CONFIRMADO no dispositivo.")
                        } else {
                            // Se não existe, marca como "não enviado"
                            viewModel.marcarAlarmeComoNaoEnviado(alarme.id)
                            Log.w("VerificaAlarmes", "Alarme '${alarme.nomeAlarme}' (ID: ${alarme.id}) NÃO encontrado no dispositivo.")
                        }
                    } else {
                        // A chamada falhou (erro 404, 500, etc.)
                        Log.e("VerificaAlarmes", "Erro ao consultar alarme '${alarme.nomeAlarme}': ${response.message()}")
                        viewModel.marcarAlarmeComoNaoEnviado(alarme.id) // Marca como não enviado por precaução
                    }
                } catch (e: IOException) {
                    // Erro de rede (sem conexão)
                    Log.e("VerificaAlarmes", "Erro de conexão ao consultar '${alarme.nomeAlarme}': ${e.message}")
                    // Se não há conexão, não podemos saber o status.
                    // Vamos manter o status anterior, ou marcar como não enviado.
                    // Marcar como não enviado parece mais seguro.
                    viewModel.marcarAlarmeComoNaoEnviado(alarme.id)
                } catch (e: Exception) {
                    // Outro erro
                    Log.e("VerificaAlarmes", "Erro inesperado ao consultar '${alarme.nomeAlarme}': ${e.message}")
                    viewModel.marcarAlarmeComoNaoEnviado(alarme.id)
                }
            }
            Log.d("VerificaAlarmes", "Verificação de alarmes concluída.")
        } catch (e: Exception) {
            // Erro ao tentar pegar a lista de alarmes do banco
            Log.e("VerificaAlarmes", "Erro crítico ao buscar alarmes do banco de dados: ${e.message}")
        }
    }
}