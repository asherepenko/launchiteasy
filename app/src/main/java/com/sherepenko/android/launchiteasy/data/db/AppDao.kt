package com.sherepenko.android.launchiteasy.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sherepenko.android.launchiteasy.data.AppItem

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppItem>)

    @Delete
    suspend fun deleteApps(apps: List<AppItem>)

    @Query("DELETE FROM applications where package_name IN (:packageNames)")
    suspend fun deleteApps(vararg packageNames: String)

    @Query("DELETE FROM applications")
    suspend fun deleteAllApps()

    @Transaction
    suspend fun updateApps(apps: List<AppItem>) {
        deleteAllApps()
        insertApps(apps)
    }

    @Query("SELECT * FROM applications WHERE is_enabled = :enabledOnly ORDER BY label ASC")
    suspend fun getAllApps(enabledOnly: Boolean = true): List<AppItem>
}
