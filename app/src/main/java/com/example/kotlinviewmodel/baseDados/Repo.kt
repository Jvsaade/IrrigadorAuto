package com.example.kotlinviewmodel.baseDados

import kotlinx.coroutines.flow.Flow

class Repository(private val Configuration_dao : ConfigurationDao){
    val allItems : Flow<List<Configuration>> = Configuration_dao.getAll()

    suspend fun insert(num: Configuration) {
        Configuration_dao.upsert(num)
    }

    suspend fun delete(num: Configuration) {
        Configuration_dao.delete(num)
    }

    suspend fun getItemById(id: Int): Configuration? {
        return Configuration_dao.getItemById(id)
    }

    suspend fun deleteAll(){
        Configuration_dao.deleteAll()
    }
}