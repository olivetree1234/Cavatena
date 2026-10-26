package com.example.violinpracticeapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "practice_sessions",
    foreignKeys = [
        ForeignKey(
            entity = PracticePlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["practicePlanId"],
            onDelete = ForeignKey.Companion.CASCADE
        ),

        ForeignKey(
            entity = LibraryItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["libraryItemId"],
            onDelete = ForeignKey.Companion.SET_NULL
        )
    ],
    indices = [
        Index("practicePlanId"),
        Index("libraryItemId")
    ]
)
data class PracticeSessionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,

    val practicePlanId: Int,

    val libraryItemId: Int?,

    val category: String,

    val title: String,

    val plannedMinutes: Int,

    val completedSeconds: Int,

    val remainingSeconds: Int,

    val completed: Boolean,

    val createdAt: Long = System.currentTimeMillis()
)