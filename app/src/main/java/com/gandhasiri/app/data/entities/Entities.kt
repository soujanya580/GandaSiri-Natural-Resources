package com.gandhasiri.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gandhasiri.app.data.database.Converters
import java.util.Date

@Entity(tableName = "trees")
@TypeConverters(Converters::class)
data class SandalwoodTree(
    @PrimaryKey
    val treeId: String,           // Unique Tree ID like "GSIRI-2024-0001"
    val nickname: String,
    val photoUri: String?,
    val latitude: Double,
    val longitude: Double,
    val girthCm: Double,          // Girth in centimeters
    val heightM: Double,          // Estimated height in meters
    val plantedDate: Date,
    val registeredDate: Date,
    val locationDescription: String,
    val notes: String,
    val isAlive: Boolean = true,
    val lastChecked: Date = Date()
)

@Entity(tableName = "growth_records")
@TypeConverters(Converters::class)
data class GrowthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val treeId: String,
    val recordedDate: Date,
    val girthCm: Double,
    val heightM: Double,
    val healthStatus: HealthStatus,
    val notes: String
)

enum class HealthStatus {
    EXCELLENT, GOOD, FAIR, POOR, CRITICAL
}

@Entity(tableName = "security_alerts")
@TypeConverters(Converters::class)
data class SecurityAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alertTime: Date,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val notifiedContacts: List<String>,
    val resolved: Boolean = false
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val relationship: String
)
