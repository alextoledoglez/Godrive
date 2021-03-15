package com.example.godrive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.godrive.data.dao.PersonDao
import com.example.godrive.data.models.Person

@Database(
    entities = [
        Person::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        private var instance: AppDatabase? = null
        private var databaseName: String = "Godrive"

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    databaseName
                ).build()
            }
            return instance!!
        }

        fun getDatabaseName(): String {
            return databaseName
        }
    }
}