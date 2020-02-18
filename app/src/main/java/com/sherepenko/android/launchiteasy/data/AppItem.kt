package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applications")
data class AppItem(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "is_system")
    val isSystem: Boolean,
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean
)