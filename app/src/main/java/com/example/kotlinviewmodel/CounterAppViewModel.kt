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
                val activeAlarms = allAlarms.first().filter { it.ativo } // Obter alarmes ativos
                if (activeAlarms.isEmpty()) {
                    _statusMessage.value = "Nenhum alarme ativo para enviar."
                    return@launch
                }

                activeAlarms.forEach { alarm ->
                    try {
                        // Usar o novo metodo setAlarmJson para enviar o objeto Configuration
                        val response = IntApi.intService.setAlarmJson(alarm)
                        if (response.isSuccessful) {
                            _statusMessage.value = "Alarme '${alarm.nomeAlarme}' enviado com sucesso (JSON)."
                        } else {
                            _statusMessage.value = "Falha ao enviar alarme '${alarm.nomeAlarme}' (JSON): ${response.message()}"
                        }
                    } catch (e: IOException) {
                        _statusMessage.value = "Erro de conexão ao enviar alarme '${alarm.nomeAlarme}' (JSON): ${e.message}"
                    } catch (e: Exception) {
                        _statusMessage.value = "Erro inesperado ao enviar alarme '${alarm.nomeAlarme}' (JSON): ${e.message}"
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
        // Esta função parece sempre retornar true. Você pode querer implementar uma lógica real aqui
        // para verificar se o alarme está realmente salvo, por exemplo, comparando com uma lista de alarmes salvos.
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