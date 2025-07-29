package com.example.kotlinviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinviewmodel.baseDados.Configuration
import com.example.kotlinviewmodel.baseDados.Repository
import com.example.kotlinviewmodel.network.IntApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    // State for the temporary message
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

    fun verificarBateria() {
        viewModelScope.launch {
            try {
                val response = IntApi.intService.verificarBateria()
                if (response.isSuccessful && response.body() != null) {
                    _statusMessage.value = response.body()
                } else {
                    _statusMessage.value = "Erro: Resposta não recebida do servidor."
                }
            } catch (e: IOException) {
                _statusMessage.value = "Erro de conexão com o dispositivo."
            } catch (e: Exception) {
                _statusMessage.value = "Ocorreu um erro inesperado."
            }
        }
    }

    fun onStatusMessageShown() {
        _statusMessage.value = null
    }

    fun toggleAtivo(){
        _config.value = _config.value.copy(ativo = !_config.value.ativo)
    }

    fun verificarTimers(){
        viewModelScope.launch {
            try {
                val activeAlarms = allAlarms.first().filter { it.ativo } // Get current active alarms
                if (activeAlarms.isEmpty()) {
                    _statusMessage.value = "Nenhum alarme ativo para enviar."
                    return@launch
                }

                activeAlarms.forEach { alarm ->
                    try {
                        val response = IntApi.intService.setAlarm(
                            nomeAlarme = alarm.nomeAlarme,
                            ativo = alarm.ativo,
                            diasSemana = alarm.diasSemana
                        )
                        if (response.isSuccessful) {
                            _statusMessage.value = "Alarme '${alarm.nomeAlarme}' enviado com sucesso."
                        } else {
                            _statusMessage.value = "Falha ao enviar alarme '${alarm.nomeAlarme}': ${response.message()}"
                        }
                    } catch (e: IOException) {
                        _statusMessage.value = "Erro de conexão ao enviar alarme '${alarm.nomeAlarme}': ${e.message}"
                    } catch (e: Exception) {
                        _statusMessage.value = "Erro inesperado ao enviar alarme '${alarm.nomeAlarme}': ${e.message}"
                    }
                }
            } catch (e: Exception) {
                _statusMessage.value = "Erro ao carregar alarmes: ${e.message}"
            }
        }
    }

    fun salvarAlarme(){
        viewModelScope.launch {
            try {
                repository.insert(config.value)
                _statusMessage.value = "Alarme salvo com sucesso!"
            } catch (e: IOException){
                _statusMessage.value = "Erro de Salvamento: ${e.message}"
            }
        }
    }

    fun excluirAlarme(alarm: Configuration) {
        viewModelScope.launch {
            try {
                repository.delete(alarm)
                _statusMessage.value = "Alarme '${alarm.nomeAlarme}' excluído com sucesso."
            } catch (e: IOException) {
                _statusMessage.value = "Erro ao deletar alarme: ${e.message}"
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
                _statusMessage.value = "Todos os alarmes foram excluídos."
            }catch (e:IOException){
                _statusMessage.value = "Erro para deletar todos os alarmes: ${e.message}"
            }
        }
    }

    fun loadAlarmForEditing(alarmId: Int) {
        viewModelScope.launch {
            val alarm = repository.getItemById(alarmId)
            alarm?.let {
                _config.value = it
            }
        }
    }

    fun resetConfig() {
        _config.value = Configuration(
            nomeAlarme = "",
            ativo = false,
            diasSemana = "0000000"
        )
    }
}