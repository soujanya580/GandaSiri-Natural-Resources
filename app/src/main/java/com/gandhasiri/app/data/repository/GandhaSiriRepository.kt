package com.gandhasiri.app.data.repository

import com.gandhasiri.app.data.dao.*
import com.gandhasiri.app.data.entities.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class GandhaSiriRepository(
    private val treeDao: TreeDao,
    private val growthRecordDao: GrowthRecordDao,
    private val securityAlertDao: SecurityAlertDao,
    private val emergencyContactDao: EmergencyContactDao
) {
    // Tree operations
    val allTrees: Flow<List<SandalwoodTree>> = treeDao.getAllTrees()
    val activeTrees: Flow<List<SandalwoodTree>> = treeDao.getActiveTrees()

    suspend fun generateTreeId(): String {
        val count = treeDao.getTreeCount() + 1
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        return "GSIRI-$year-${count.toString().padStart(4, '0')}"
    }

    suspend fun insertTree(tree: SandalwoodTree) = treeDao.insertTree(tree)
    suspend fun updateTree(tree: SandalwoodTree) = treeDao.updateTree(tree)
    suspend fun deleteTree(tree: SandalwoodTree) = treeDao.deleteTree(tree)
    suspend fun getTreeById(treeId: String) = treeDao.getTreeById(treeId)
    fun getTreeByIdFlow(treeId: String) = treeDao.getTreeByIdFlow(treeId)

    // Growth records
    fun getGrowthRecords(treeId: String): Flow<List<GrowthRecord>> =
        growthRecordDao.getGrowthRecordsForTree(treeId)

    suspend fun insertGrowthRecord(record: GrowthRecord) =
        growthRecordDao.insertGrowthRecord(record)

    suspend fun deleteGrowthRecord(record: GrowthRecord) =
        growthRecordDao.deleteGrowthRecord(record)

    // Security alerts
    val allAlerts: Flow<List<SecurityAlert>> = securityAlertDao.getAllAlerts()
    val activeAlerts: Flow<List<SecurityAlert>> = securityAlertDao.getActiveAlerts()

    suspend fun insertAlert(alert: SecurityAlert): Long = securityAlertDao.insertAlert(alert)
    suspend fun resolveAlert(alert: SecurityAlert) =
        securityAlertDao.updateAlert(alert.copy(resolved = true))

    // Emergency contacts
    val allContacts: Flow<List<EmergencyContact>> = emergencyContactDao.getAllContacts()

    suspend fun insertContact(contact: EmergencyContact) =
        emergencyContactDao.insertContact(contact)

    suspend fun updateContact(contact: EmergencyContact) =
        emergencyContactDao.updateContact(contact)

    suspend fun deleteContact(contact: EmergencyContact) =
        emergencyContactDao.deleteContact(contact)

    suspend fun getAllContactsOnce(): List<EmergencyContact> =
        emergencyContactDao.getAllContactsOnce()
}
