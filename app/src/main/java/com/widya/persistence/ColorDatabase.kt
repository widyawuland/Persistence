package com.widya.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//menambahkan entitas disini entities = [Color::class]
@Database(entities = [Color::class], version = 1, exportSchema = false)
abstract class ColorDatabase : RoomDatabase() {
    //menambahkan dao di sini dengan cara menuliskan abstract fun colorDao(): ColorDao(color diganti nama lain)
    abstract fun colorDao(): ColorDao
    companion object {
        @Volatile
        private var Instance: ColorDatabase? = null

        fun getInstances(context: Context): ColorDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, ColorDatabase::class.java, "database-color")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}