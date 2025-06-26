package com.example.kotlinviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinviewmodel.baseDados.Configuration
import com.example.kotlinviewmodel.baseDados.ConfigurationDao
import com.example.kotlinviewmodel.baseDados.Repository
import com.example.kotlinviewmodel.network.IntApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.IOException

class CounterAppViewModel(private val repository: Repository) : ViewModel() {

    private val _texto = mutableStateOf("")
    val texto: MutableState<String> = _texto

    private val _config = mutableStateOf(
        Configuration(
            nomeAlarme = "",
            ativo = false,
            diasSemana = "0000000"
        )
    )
    var config: MutableState<Configuration> = _config

    val allAlarms: Flow<List<Configuration>> = repository.allItems

    fun updateDiaSemana(dia: Int){
        val dias = _config.value.diasSemana.toCharArray()
        // Inverte o valor na posição indicada
        dias[dia] = if (dias[dia] == '1') '0' else '1'
        _config.value = _config.value.copy(diasSemana = String(dias)) // Cria uma nova string com a modificação
    }

    fun updateNome(str: String){
        _config.value = _config.value.copy(nomeAlarme = str)
    }

    //    init {
//        loadInitialCount()
//    }
    // Essa função vai fazer uma requisição HTTP para o microcontrolador
    // para enviar informações sobre os timers.
    fun atualizarTimers(){

        verificarTimers()
    }

    // Essa função torna o alarme ativo
    fun toggleAtivo(){
        _config.value = _config.value.copy(ativo = !_config.value.ativo)
    }


    // Essa função verifica os timers existentes e retorna se todos foram
    // configurados corretamente
    fun verificarTimers(){

    }

    fun salvarAlarme(){
        viewModelScope.launch {
            try {
                repository.insert(config.value)
            } catch (e: IOException){
                println("Erro de Salvamento")
            }
        }
    }

    // Nova função para excluir um alarme específico
    fun excluirAlarme(alarm: Configuration) {
        viewModelScope.launch {
            try {
                repository.delete(alarm)
            } catch (e: IOException) {
                println("Erro ao deletar alarme: ${e.message}")
            }
        }
    }

    fun alarmeSalvo():Boolean{
        return true
    }

    fun excluirTodosAlarmes(){
        viewModelScope.launch {
            try{
                repository.deleteAll()
            }catch (e:IOException){
                println("Erro para deletar")
            }
        }
    }


}