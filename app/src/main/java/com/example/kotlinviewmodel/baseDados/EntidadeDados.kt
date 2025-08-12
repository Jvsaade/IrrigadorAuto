package com.example.kotlinviewmodel.baseDados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

@Entity
data class Configuration(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    @ColumnInfo(name = "Nome do alarme") val nomeAlarme: String,
    @ColumnInfo(name = "Hora do alarme") val horaAlarme: Int,
    @ColumnInfo(name = "Minuto do alarme") val minutoAlarme: Int,
    @ColumnInfo(name = "Duração do alarme") val duracaoAlarme: Int,
    @ColumnInfo(name = "Ativação") val ativo: Boolean,
    @ColumnInfo(name = "Dias da Semana") val diasSemana: String
)