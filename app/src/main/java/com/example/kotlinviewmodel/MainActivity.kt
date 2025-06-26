package com.example.kotlinviewmodel

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlinviewmodel.baseDados.BaseDados
import com.example.kotlinviewmodel.baseDados.Repository
import com.example.kotlinviewmodel.ui.theme.KotlinViewModelTheme

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
        composable("Tela2") {
            SecondScreen(viewModel = viewModel, navController = navController)
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