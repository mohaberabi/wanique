package com.mohaberabi.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RunPendingEntity(
    @Embedded val run: RunEntity,
    @PrimaryKey(autoGenerate = false)
    val runId: String = run.id,
    val mapPictureBytes: ByteArray,
    val userId: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RunPendingEntity

        if (run != other.run) return false
        if (runId != other.runId) return false
        if (!mapPictureBytes.contentEquals(other.mapPictureBytes)) return false
        return userId == other.userId
    }

    override fun hashCode(): Int {
        var result = run.hashCode()
        result = 31 * result + runId.hashCode()
        result = 31 * result + mapPictureBytes.contentHashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}