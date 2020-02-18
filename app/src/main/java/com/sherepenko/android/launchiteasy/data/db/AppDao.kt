package com.sherepenko.android.launchiteasy.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sherepenko.android.launchiteasy.data.AppItem

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(vararg apps: AppItem)

    @Update
    suspend fun updateApps(vararg apps: AppItem)

    @Delete
    suspend fun deleteApps(vararg apps: AppItem)

    @Query("DELETE FROM applications where package_name IN (:packageNames)")
    suspend fun deleteApps(vararg packageNames: String)

    @Query("DELETE FROM applications")
    suspend fun deleteAllApps()

    @Query("SELECT * FROM applications WHERE is_enabled = :enabledOnly ORDER BY label ASC")
    suspend fun getAllApps(enabledOnly: Boolean = true): List<AppItem>
}
