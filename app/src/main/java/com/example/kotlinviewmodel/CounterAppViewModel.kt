package com.example.kotlinviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinviewmodel.baseDados.Configuration
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

    // 1. State for the temporary message
    private val _statusMessage = mutableStateOf<String?>(null)
    val statusMessage: MutableState<String?> = _statusMessage

    fun updateDiaSemana(dia: Int){
        val dias = _config.value.diasSemana.toCharArray()
        dias[dia] = if (dias[dia] == '1') '0' else '1'
        _config.value = _config.value.copy(diasSemana = String(dias))
    }

    fun updateNome(str: String){
        _config.value = _config.value.copy(nomeAlarme = str)
    }

    fun atualizarTimers(){
        verificarTimers()
    }

    // 2. Implement the network call logic
    fun verificarBateria() {
        viewModelScope.launch {
            try {
                val response = IntApi.intService.verificarBateria()
                if (response.isSuccessful && response.body() != null) {
                    // Success: Update state with the server message
                    _statusMessage.value = response.body()
                } else {
                    // Error: Handle unsuccessful server responses
                    _statusMessage.value = "Erro: Resposta não recebida do servidor."
                }
            } catch (e: IOException) {
                // Error: Handle network connection issues
                _statusMessage.value = "Erro de conexão com o dispositivo."
            } catch (e: Exception) {
                // Error: Handle any other unexpected errors
                _statusMessage.value = "Ocorreu um erro inesperado."
            }
        }
    }

    // 3. Function to reset the message state after it's shown
    fun onStatusMessageShown() {
        _statusMessage.value = null
    }

    fun toggleAtivo(){
        _config.value = _config.value.copy(ativo = !_config.value.ativo)
    }

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
        // This function seems to always return true. You might want to implement actual logic here
        // to check if the alarm is truly saved, e.g., by comparing with a list of saved alarms.
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

    // New function to load an alarm for editing
    fun loadAlarmForEditing(alarmId: Int) {
        viewModelScope.launch {
            val alarm = repository.getItemById(alarmId)
            alarm?.let {
                _config.value = it
            }
        }
    }

    // New function to reset config for a new alarm
    fun resetConfig() {
        _config.value = Configuration(
            nomeAlarme = "",
            ativo = false,
            diasSemana = "0000000"
        )
    }
}