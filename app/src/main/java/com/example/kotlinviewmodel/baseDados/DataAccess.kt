package com.example.kotlinviewmodel.baseDados

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigurationDao{
    @Query("SELECT * FROM configuration")
    fun getAll(): Flow<List<Configuration>>

    @Upsert
    suspend fun upsert(num: Configuration)

    @Delete
    suspend fun delete(num: Configuration)

    @Query("DELETE FROM configuration")
    suspend fun deleteAll()

    @Query("SELECT * FROM Configuration WHERE id = :id")
    suspend fun getItemById(id: Int): Configuration?

    @Query("SELECT * FROM Configuration WHERE `Nome do alarme` = :nome")
    suspend fun getItemByName(nome: String): Configuration?
}