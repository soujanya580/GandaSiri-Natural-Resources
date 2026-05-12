package com.gandhasiri.app.data.dao

import androidx.room.*
import com.gandhasiri.app.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TreeDao {
    @Query("SELECT * FROM trees ORDER BY registeredDate DESC")
    fun getAllTrees(): Flow<List<SandalwoodTree>>

    @Query("SELECT * FROM trees WHERE treeId = :treeId")
    suspend fun getTreeById(treeId: String): SandalwoodTree?

    @Query("SELECT * FROM trees WHERE treeId = :treeId")
    fun getTreeByIdFlow(treeId: String): Flow<SandalwoodTree?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTree(tree: SandalwoodTree)

    @Update
    suspend fun updateTree(tree: SandalwoodTree)

    @Delete
    suspend fun deleteTree(tree: SandalwoodTree)

    @Query("SELECT COUNT(*) FROM trees")
    suspend fun getTreeCount(): Int

    @Query("SELECT * FROM trees WHERE isAlive = 1")
    fun getActiveTrees(): Flow<List<SandalwoodTree>>
}

@Dao
interface GrowthRecordDao {
    @Query("SELECT * FROM growth_records WHERE treeId = :treeId ORDER BY recordedDate ASC")
    fun getGrowthRecordsForTree(treeId: String): Flow<List<GrowthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowthRecord(record: GrowthRecord)

    @Delete
    suspend fun deleteGrowthRecord(record: GrowthRecord)

    @Query("SELECT * FROM growth_records WHERE treeId = :treeId ORDER BY recordedDate DESC LIMIT 1")
    suspend fun getLatestGrowthRecord(treeId: String): GrowthRecord?
}

@Dao
interface SecurityAlertDao {
    @Query("SELECT * FROM security_alerts ORDER BY alertTime DESC")
    fun getAllAlerts(): Flow<List<SecurityAlert>>

    @Insert
    suspend fun insertAlert(alert: SecurityAlert): Long

    @Update
    suspend fun updateAlert(alert: SecurityAlert)

    @Query("SELECT * FROM security_alerts WHERE resolved = 0 ORDER BY alertTime DESC")
    fun getActiveAlerts(): Flow<List<SecurityAlert>>
}

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact)

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("SELECT * FROM emergency_contacts")
    suspend fun getAllContactsOnce(): List<EmergencyContact>
}
