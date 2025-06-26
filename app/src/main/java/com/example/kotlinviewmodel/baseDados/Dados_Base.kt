package com.example.kotlinviewmodel.baseDados

import android.content.Context
import android.provider.ContactsContract.Contacts
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Configuration::class],
    version = 1
)
abstract class BaseDados: RoomDatabase(){
    abstract val ConfigurationDao: ConfigurationDao

    companion object {
        @Volatile
        private var INSTANCE: BaseDados? = null

        fun getDatabase(context: Context): BaseDados {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDados::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
