package com.gandhasiri.app.viewmodel

import android.app.Application
import android.content.Context
import android.telephony.SmsManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gandhasiri.app.data.database.GandhaSiriDatabase
import com.gandhasiri.app.data.entities.*
import com.gandhasiri.app.data.repository.GandhaSiriRepository
import com.gandhasiri.app.utils.HeartwoodEstimate
import com.gandhasiri.app.utils.NotificationHelper
import com.gandhasiri.app.utils.TreeCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class GandhaSiriViewModel(application: Application) : AndroidViewModel(application) {

    private val db = GandhaSiriDatabase.getDatabase(application)
    private val repository = GandhaSiriRepository(
        db.treeDao(),
        db.growthRecordDao(),
        db.securityAlertDao(),
        db.emergencyContactDao()
    )

    // Trees
    val allTrees: StateFlow<List<SandalwoodTree>> = repository.allTrees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Alerts
    val activeAlerts: StateFlow<List<SecurityAlert>> = repository.activeAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Contacts
    val allContacts: StateFlow<List<EmergencyContact>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI State
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    fun clearMessage() { _uiMessage.value = null }

    // ======= TREE OPERATIONS =======

    fun addTree(
        nickname: String,
        photoUri: String?,
        latitude: Double,
        longitude: Double,
        girthCm: Double,
        heightM: Double,
        plantedDate: Date,
        locationDescription: String,
        notes: String
    ) = viewModelScope.launch {
        val treeId = repository.generateTreeId()
        val tree = SandalwoodTree(
            treeId = treeId,
            nickname = nickname,
            photoUri = photoUri,
            latitude = latitude,
            longitude = longitude,
            girthCm = girthCm,
            heightM = heightM,
            plantedDate = plantedDate,
            registeredDate = Date(),
            locationDescription = locationDescription,
            notes = notes
        )
        repository.insertTree(tree)

        // Also add initial growth record
        val growthRecord = GrowthRecord(
            treeId = treeId,
            recordedDate = Date(),
            girthCm = girthCm,
            heightM = heightM,
            healthStatus = HealthStatus.GOOD,
            notes = "Initial registration"
        )
        repository.insertGrowthRecord(growthRecord)

        _uiMessage.value = "Tree registered! ID: $treeId"
    }

    fun updateTree(tree: SandalwoodTree) = viewModelScope.launch {
        repository.updateTree(tree)
        _uiMessage.value = "Tree updated successfully"
    }

    fun deleteTree(tree: SandalwoodTree) = viewModelScope.launch {
        repository.deleteTree(tree)
        _uiMessage.value = "Tree removed from register"
    }

    fun getTreeById(treeId: String): Flow<SandalwoodTree?> = repository.getTreeByIdFlow(treeId)

    // ======= GROWTH RECORDS =======

    fun getGrowthRecords(treeId: String): Flow<List<GrowthRecord>> =
        repository.getGrowthRecords(treeId)

    fun addGrowthRecord(
        treeId: String,
        girthCm: Double,
        heightM: Double,
        healthStatus: HealthStatus,
        notes: String
    ) = viewModelScope.launch {
        val record = GrowthRecord(
            treeId = treeId,
            recordedDate = Date(),
            girthCm = girthCm,
            heightM = heightM,
            healthStatus = healthStatus,
            notes = notes
        )
        repository.insertGrowthRecord(record)

        // Update the tree's current measurements
        repository.getTreeById(treeId)?.let { tree ->
            repository.updateTree(tree.copy(girthCm = girthCm, heightM = heightM, lastChecked = Date()))
        }
        _uiMessage.value = "Growth record added"
    }

    // ======= SECURITY ALERTS =======

    fun triggerPanicAlert(
        context: Context,
        latitude: Double,
        longitude: Double,
        description: String
    ) = viewModelScope.launch {
        val contacts = repository.getAllContactsOnce()
        val contactNumbers = contacts.map { it.phone }
        val contactNames = contacts.map { it.name }

        val alert = SecurityAlert(
            alertTime = Date(),
            latitude = latitude,
            longitude = longitude,
            description = description,
            notifiedContacts = contactNames
        )
        repository.insertAlert(alert)

        // Send notification
        NotificationHelper.sendSecurityAlert(
            context,
            description,
            "Lat: ${"%.4f".format(latitude)}, Lon: ${"%.4f".format(longitude)}"
        )

        // Simulate SMS (attempt real SMS if permission granted)
        val smsMessage = """
            🚨 GANDHA-SIRI SECURITY ALERT!
            
            Suspicious activity reported near sandalwood farm.
            Description: $description
            Location: Lat ${"%.4f".format(latitude)}, Lon ${"%.4f".format(longitude)}
            Time: ${Date()}
            
            Please check immediately or contact police at 100.
        """.trimIndent()

        contactNumbers.forEach { number ->
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(number, null, smsMessage, null, null)
            } catch (e: Exception) {
                // SMS failed — notification was already sent
            }
        }

        _uiMessage.value = "🚨 ALERT SENT! ${contacts.size} contacts notified."
    }

    fun resolveAlert(alert: SecurityAlert) = viewModelScope.launch {
        repository.resolveAlert(alert)
        _uiMessage.value = "Alert marked as resolved"
    }

    // ======= CONTACTS =======

    fun addContact(name: String, phone: String, relationship: String) = viewModelScope.launch {
        repository.insertContact(EmergencyContact(name = name, phone = phone, relationship = relationship))
        _uiMessage.value = "Contact added"
    }

    fun deleteContact(contact: EmergencyContact) = viewModelScope.launch {
        repository.deleteContact(contact)
        _uiMessage.value = "Contact removed"
    }

    // ======= CALCULATIONS =======

    fun calculateHeartwoodEstimate(girthCm: Double, plantedDate: Date): HeartwoodEstimate {
        val ageMs = Date().time - plantedDate.time
        val ageYears = (ageMs / (365.25 * 24 * 60 * 60 * 1000)).toInt()
        return TreeCalculator.calculateHeartwoodEstimate(girthCm, ageYears)
    }

    fun getTreeAgeYears(plantedDate: Date): Int {
        val ageMs = Date().time - plantedDate.time
        return (ageMs / (365.25 * 24 * 60 * 60 * 1000)).toInt()
    }
}
