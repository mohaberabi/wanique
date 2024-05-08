package com.mohaberabi.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeletedRunEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String,
    val userId: String
)
