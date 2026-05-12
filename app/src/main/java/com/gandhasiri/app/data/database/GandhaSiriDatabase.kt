package com.gandhasiri.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gandhasiri.app.data.dao.*
import com.gandhasiri.app.data.entities.*

@Database(
    entities = [
        SandalwoodTree::class,
        GrowthRecord::class,
        SecurityAlert::class,
        EmergencyContact::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GandhaSiriDatabase : RoomDatabase() {
    abstract fun treeDao(): TreeDao
    abstract fun growthRecordDao(): GrowthRecordDao
    abstract fun securityAlertDao(): SecurityAlertDao
    abstract fun emergencyContactDao(): EmergencyContactDao

    companion object {
        @Volatile
        private var INSTANCE: GandhaSiriDatabase? = null

        fun getDatabase(context: Context): GandhaSiriDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GandhaSiriDatabase::class.java,
                    "gandhasiri_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
